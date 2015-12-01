package com.ft.report.web;

import com.ft.report.model.Desk;
import com.ft.report.model.Project;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@ConfigurationProperties(prefix = "report")
public class ProjectFormatter implements Formatter<Project> {
    @Setter
    @Getter
    private Map<String, Desk> desks;
    private List<Project> projects = new ArrayList<>();

    @PostConstruct
    public void config() {
        Stream<List<Project>> projectStream = desks.entrySet().stream()
                .map(stringDeskEntry -> stringDeskEntry.getValue().getProjects());
        projects = projectStream.flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public Project parse(String text, Locale locale) throws ParseException {
        Long id = Long.parseLong(text);
        return projects.stream()
                .filter( p -> p.getId().equals(id))
                .findFirst()
                .orElse(new Project(id));
    }

    @Override
    public String print(Project project, Locale locale) {
        return project.getId().toString();
    }
}
