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

    public Map<String, List<ReportTask>> sort(String team, Map<String, List<ReportTask>> result) {
        Map<String, List<ReportTask>> sortedResult = new LinkedHashMap<>();
        List<String> tags = desks.get(team).getPremiumTags();

        if (!arePremiumTagsConfigured(tags)) {
            tags = addAllTags(result);
        } else {
            tags.add(ReportGenerator.OTHERS_TAG);
        }
        tags.add(ReportGenerator.NOT_TAGGED_TAG);

        tags.stream().forEach(premiumTag -> {
                    List<ReportTask> reportTasks = result.get(premiumTag);
                    if (reportTasks != null) {
                        sortedResult.put(premiumTag, reportTasks);
                    }
                }
        );
        return sortedResult;
    }

    private boolean arePremiumTagsConfigured(List<String> premiumTags) {
        return premiumTags != null;
    }

    private List<String> addAllTags(Map<String, List<ReportTask>> result) {
        return result.keySet().stream()
                .filter(key -> !key.equals(ReportGenerator.NOT_TAGGED_TAG))
                .collect(Collectors.toList());
    }
}
