package com.ft.report;

import com.ft.asanaapi.AsanaClientWrapper;
import com.ft.asanaapi.model.Tag;
import com.ft.report.date.DueDatePredicateFactory;
import com.ft.report.model.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConfigurationProperties(prefix = "report")
@Component
public class ReportGenerator {
    public static final String OTHERS_TAG = "Others";
    public static final String NOT_TAGGED_TAG = "Not tagged";
    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    @Autowired
    private AsanaClientWrapper defaultAsanaClientWrapper;
    @Autowired
    private DueDatePredicateFactory dueDatePredicateFactory;
    @Autowired
    private ReportSorter reportSorter;

    @Setter @Getter
    private Map<String, Desk> desks;

    public List<Report> generate(Criteria criteria) {

        List<Report> reports = new LinkedList<>();
        for (Project project : criteria.getProjects()) {
            Report report = generate(criteria.getTeam(), project, criteria.getReportType());
            reports.add(report);
        }
        return reports;
    }

    public Report generate(String teamName, Project project, ReportType reportType) {

        Report report = new Report();
        report.setProject(project);
        report.setGroupByTags(shouldGroupByTags(teamName));

        List<ReportTask> reportTasks;
        try {
            reportTasks = defaultAsanaClientWrapper.getReportTasks(project.getId().toString());
        } catch (IOException e) {
            logger.warn("Could not fetch report tasks for project: " + project.getId(), e);
            reportTasks = Collections.emptyList();
        }
        Stream<ReportTask> reportTaskStream = reportTasks.stream()
                .filter(rt -> rt.getDue_on() != null)
                .filter(dueDatePredicateFactory.create(reportType));

        Map<String, List<ReportTask>> unsortedTasks = report.isGroupByTags() ? toTagsMap(teamName, reportTaskStream) : toOneTagMap(reportTaskStream);
        Map<String, List<ReportTask>> sortedResult = reportSorter.sort(teamName, unsortedTasks);
        report.setTagTasks(sortedResult);

        return report;
    }

    private Map<String, List<ReportTask>> toTagsMap(String team, Stream<ReportTask> reportTaskStream) {
        return reportTaskStream
                .collect(Collectors.groupingBy(rt -> extractTagName(team, rt.getTags())));
    }

    private Map<String, List<ReportTask>> toOneTagMap(Stream<ReportTask> reportTaskStream) {
        return reportTaskStream
                .collect(Collectors.groupingBy(rt -> NOT_TAGGED_TAG));
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
