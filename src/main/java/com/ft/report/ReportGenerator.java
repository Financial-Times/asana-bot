package com.ft.report;

import com.ft.asanaapi.model.Tag;
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
    private static final String OTHERS_TAG = "Others";
    private static final String NOT_TAGGED_TAG = "Not tagged";

    private static final String COMPLETED_SINCE_NOW = "now"; //For Asana it means not completed

    @Autowired private AsanaService asanaService;
    @Autowired private DueDatePredicateFactory dueDatePredicateFactory;
    @Setter
    @Getter
    private Map<String, Desk> desks;
    @Setter private Clock clock = Clock.systemDefaultZone();


    public Report generate(ReportType reportType, String team) {

        String projectId = desks.get(team).getProjectId();
        List<ReportTask> reportTasks = asanaService.findTasks(projectId, COMPLETED_SINCE_NOW);

        Stream<ReportTask> reportTaskStream = reportTasks.stream()
                .filter(dueDatePredicateFactory.create(reportType));

        Map<String, List<ReportTask>> result = reportTaskStream
                .collect(Collectors.groupingBy(rt -> extractTagName(team, rt.getTags())) );

        Report report = new Report();
        report.setTagTasks(result);
        return report;
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
