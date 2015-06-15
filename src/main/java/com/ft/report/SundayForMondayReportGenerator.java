package com.ft.report;

import com.ft.asanaapi.model.Tag;
import com.ft.services.AsanaService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConfigurationProperties(prefix = "report")
@Component
public class SundayForMondayReportGenerator implements ReportGenerator {

    private static final String OTHERS_TAG = "Others";
    private static final String NOT_TAGGED_TAG = "Not tagged";
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String COMPLETED_SINCE_NOW = "now"; //For Asana it means not completed

    @Autowired private AsanaService asanaService;
    @Setter @Getter private Map<String, Desk> desks;
    @Setter private Clock clock = Clock.systemDefaultZone();

    @Override
    public Report generate(String team) {

        String projectId = desks.get(team).getProjectId();
        List<ReportTask> reportTasks = asanaService.findTasks(projectId, COMPLETED_SINCE_NOW);

        LocalDate today = LocalDate.now(clock);
        String sunday = getNextDay(today, DayOfWeek.SUNDAY);
        String monday = getNextDay(today, DayOfWeek.MONDAY);
        Stream<ReportTask> reportTaskStream = reportTasks.stream()
                .filter(rt -> sunday.equals(rt.getDue_on()) || monday.equals(rt.getDue_on()));

        Map<String, List<ReportTask>> result = reportTaskStream
                .collect(Collectors.groupingBy( rt -> extractTagName(team, rt.getTags()) ) );

        Report report = new Report();
        report.setTagTasks(result);
        return report;
    }

    private String getNextDay(LocalDate day, DayOfWeek nextDayOfWeek) {
        return day.with(TemporalAdjusters.next(nextDayOfWeek)).format(dateFormat);
    }

    private String extractTagName(String team, List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return NOT_TAGGED_TAG;
        }

        List<String> premiumCompaniesTags = desks.get(team).getPremiumTags();
        if (premiumCompaniesTags == null || premiumCompaniesTags.isEmpty()) {
            Tag firstTag = tags.remove(0);
            return firstTag.getName();
        }

        Optional<Tag> candidate = tags.stream().filter(tag -> premiumCompaniesTags.contains(tag.getName())).findFirst();
        if (candidate.isPresent()) {
            tags.remove(candidate.get());
            return candidate.get().getName();
        }

        return OTHERS_TAG;
    }
}