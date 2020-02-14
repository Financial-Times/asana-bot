package com.ft.report;

import com.asana.models.Tag;
import com.ft.asanaapi.AsanaClientWrapper;
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
    static final String OTHERS_TAG = "Others";
    static final String NOT_TAGGED_TAG = "Not tagged";
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

    private Map<String, Integer> sectionsLoopUP = Stream.of(
            new AbstractMap.SimpleEntry<>(1, "HONG KONG"),
            new AbstractMap.SimpleEntry<>(2, "MARKETS"),
            new AbstractMap.SimpleEntry<>(3, "COMPANIES"),
            new AbstractMap.SimpleEntry<>(4, "TECH"),
            new AbstractMap.SimpleEntry<>(5, "LEX"),
            new AbstractMap.SimpleEntry<>(6, "WORLD"),
            new AbstractMap.SimpleEntry<>(7, "UK"),
            new AbstractMap.SimpleEntry<>(8, "BIG READ"),
            new AbstractMap.SimpleEntry<>(9, "COMMENT"),
            new AbstractMap.SimpleEntry<>(10, "VIDEO"),
            new AbstractMap.SimpleEntry<>(11, "SPECIAL REPORTS"),
            new AbstractMap.SimpleEntry<>(12, "MAGAZINE"),
            new AbstractMap.SimpleEntry<>(13, "LIFE & ARTS"),
            new AbstractMap.SimpleEntry<>(14, "HOUSE & HOME"),
            new AbstractMap.SimpleEntry<>(15, "MONEY")).collect(Collectors.toMap(AbstractMap.SimpleEntry::getValue, AbstractMap.SimpleEntry::getKey));

    public Report generate(String teamName, Project project, ReportType reportType) {

        Report report = new Report();
        updateProject(project);
        report.setProject(project);
        report.setGroupByTags(shouldGroupByTags(teamName));
        report.setGroupBySections(shouldGroupBySections(teamName));

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

        Map<String, List<ReportTask>> unsortedTasks = report.isGroupByTags() ? toTagsMap(teamName, reportTaskStream) : report.isGroupBySections() ? toSectionsMap(reportTaskStream) : toOneTagMap(reportTaskStream);
        Map<String, List<ReportTask>> sortedResult = reportSorter.sort(teamName, unsortedTasks);
        report.setTagTasks(sortedResult);

        return report;
    }

    private void updateProject(Project project) {
        List<Project> projects = desks.values().stream().flatMap(r -> r.getProjects().stream()).collect(Collectors.toList());
        Project projectName = projects.stream().filter(r -> r.getId().contains(project.getId())).findFirst().get();
        project.setName(projectName.getName());
    }

    private Map<String, List<ReportTask>> toTagsMap(String team, Stream<ReportTask> reportTaskStream) {
        return reportTaskStream
                .collect(Collectors.groupingBy(rt -> extractTagName(team, rt.getTags())));
    }

    private Map<String, List<ReportTask>> toSectionsMap(Stream<ReportTask> reportTaskStream) {

        Map<String, List<ReportTask>> unsortedMap =  reportTaskStream
                .collect(Collectors.groupingBy(rt -> rt.getSectionName()));

        return unsortedMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey((a, b) -> {
                    Integer firstValue = sectionsLoopUP.containsKey(a) ? sectionsLoopUP.get(a) : 100;
                    Integer secondValue = sectionsLoopUP.containsKey(b) ? sectionsLoopUP.get(b) : 100;
                    return firstValue.compareTo(secondValue);
                }))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
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
            return firstTag.name;
        }

        Optional<Tag> candidate = tags.stream().filter(tag -> premiumTags.contains(tag.name)).findFirst();
        if (candidate.isPresent()) {
            tags.remove(candidate.get());
            return candidate.get().name;
        }

        return OTHERS_TAG;
    }

    private boolean shouldGroupByTags(String team) {
        desks.get(team).getPremiumTags();
        return desks.get(team).isGroupTags();
    }
    private boolean shouldGroupBySections(String team) {
        return desks.get(team).isGroupSections();
    }
}
