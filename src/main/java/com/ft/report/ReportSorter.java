package com.ft.report;

import com.ft.asanaapi.model.CustomTask;
import com.ft.report.model.Desk;
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

    public Map<String, List<CustomTask>> sort(String team, Map<String, List<CustomTask>> reportTasksToSort) {
        Map<String, List<CustomTask>> sortedResult = new LinkedHashMap<>();
        List<String> tags = setupTags(team, reportTasksToSort);

        tags.stream().forEach(tag -> {
                    List<CustomTask> reportTasks = reportTasksToSort.get(tag);
                    if (reportTasks != null) {
                        List<CustomTask> sortedReportTasks = sortByTagNameAndDueDate(reportTasks);
                        sortedResult.put(tag, sortedReportTasks);
                    }
                }
        );
        return sortedResult;
    }

    private List<String> setupTags(String team, Map<String, List<CustomTask>> reportTasksToSort) {
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

    private List<String> addAllTags(Map<String, List<CustomTask>> result) {
        return result.keySet().stream()
                .filter(key -> !key.equals(ReportGenerator.NOT_TAGGED_TAG))
                .collect(Collectors.toList());
    }

    private List<CustomTask> sortByTagNameAndDueDate(List<CustomTask> reportTasks) {
        return reportTasks.stream()
                .peek(CustomTask::assignImportant)
                .sorted(CustomTask.byImportance
                        .thenComparing(CustomTask::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(CustomTask.byDueDate))
                .collect(Collectors.toList());
    }
}
