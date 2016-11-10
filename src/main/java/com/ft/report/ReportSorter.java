package com.ft.report;

import com.ft.report.model.Desk;
import com.ft.report.model.ReportTask;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "report")
@Component
public class ReportSorter {

    @Setter
    @Getter
    private Map<String, Desk> desks;

    public Map<String, List<ReportTask>> sort(String team, Map<String, List<ReportTask>> reportTasksToSort) {
        Map<String, List<ReportTask>> sortedResult = new LinkedHashMap<>();
        List<String> tags = setupTags(team, reportTasksToSort);

        tags.stream().forEach(tag -> {
                    List<ReportTask> reportTasks = reportTasksToSort.get(tag);
                    if (reportTasks != null) {
                        List<ReportTask> sortedReportTasks = sortByTagNameAndDueDate(reportTasks);
                        sortedResult.put(tag, sortedReportTasks);
                    }
                }
        );
        return sortedResult;
    }

    private List<String> setupTags(String team, Map<String, List<ReportTask>> reportTasksToSort) {
        List<String> tags = desks.get(team).getPremiumTags();

        if (!arePremiumTagsConfigured(tags)) {
            tags = addAllTags(reportTasksToSort);
        } else {
            tags.add(ReportGenerator.OTHERS_TAG);
        }
        tags.add(ReportGenerator.NOT_TAGGED_TAG);
        return tags;
    }

    private boolean arePremiumTagsConfigured(List<String> premiumTags) {
        return premiumTags != null;
    }

    private List<String> addAllTags(Map<String, List<ReportTask>> result) {
        return result.keySet().stream()
                .filter(key -> !key.equals(ReportGenerator.NOT_TAGGED_TAG))
                .collect(Collectors.toList());
    }

    private List<ReportTask> sortByTagNameAndDueDate(List<ReportTask> reportTasks) {
        return reportTasks.stream()
                .peek(ReportTask::assignImportant)
                .sorted(ReportTask.byImportance
                        .thenComparing(ReportTask::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(ReportTask.byDueDate))
                .collect(Collectors.toList());
    }
}
