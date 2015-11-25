package com.ft.report;

import com.ft.asanaapi.model.Tag;
import com.ft.report.model.Criteria;
import com.ft.report.model.Desk;
import com.ft.report.model.Report;
import com.ft.report.model.ReportTask;
import com.ft.services.AsanaService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConfigurationProperties(prefix = "report")
@Component
public class ReportGenerator {
    public static final String OTHERS_TAG = "Others";
    public static final String NOT_TAGGED_TAG = "Not tagged";

    private static final String COMPLETED_SINCE_NOW = "now"; //For Asana it means not completed

    @Autowired private AsanaService asanaService;
    @Autowired private DueDatePredicateFactory dueDatePredicateFactory;
    @Autowired private ReportSorter reportSorter;

    @Setter
    @Getter
    private Map<String, Desk> desks;
    @Setter private Clock clock = Clock.systemUTC();


    public Report generate(Criteria criteria) {

        Report report = new Report();
        String team = criteria.getTeam();
        report.setGroupByTags(shouldGroupByTags(team));

        List<ReportTask> reportTasks = asanaService.findTasks(criteria.getProject().getId(), COMPLETED_SINCE_NOW);
        Stream<ReportTask> reportTaskStream = reportTasks.stream()
                .filter(rt -> rt.getDue_on() != null)
                .filter(dueDatePredicateFactory.create(criteria.getReportType()));

        Map<String, List<ReportTask>> unsortedTasks = report.isGroupByTags() ? toTagsMap(team, reportTaskStream) : toOneTagMap(reportTaskStream);
        Map<String, List<ReportTask>> sortedResult = reportSorter.sort(team, unsortedTasks);
        report.setTagTasks(sortedResult);

        return report;
    }

    private Map<String, List<ReportTask>> toTagsMap(String team, Stream<ReportTask> reportTaskStream) {
        return reportTaskStream
                .collect(Collectors.groupingBy(rt -> extractTagName(team, rt.getTags())) );
    }

    private Map<String, List<ReportTask>> toOneTagMap(Stream<ReportTask> reportTaskStream) {
        return reportTaskStream
                .collect(Collectors.groupingBy(rt -> NOT_TAGGED_TAG) );
    }

    private String extractTagName(String team, List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return NOT_TAGGED_TAG;
        }

        List<String> premiumTags = desks.get(team).getPremiumTags();
        if (premiumTags == null || premiumTags.isEmpty()) {
            Tag firstTag = tags.remove(0);
            return firstTag.getName();
        }

        Optional<Tag> candidate = tags.stream().filter(tag -> premiumTags.contains(tag.getName())).findFirst();
        if (candidate.isPresent()) {
            tags.remove(candidate.get());
            return candidate.get().getName();
        }

        return OTHERS_TAG;
    }

    private boolean shouldGroupByTags(String team) {
        desks.get(team).getPremiumTags();
        return desks.get(team).isGroupTags();
    }
}
