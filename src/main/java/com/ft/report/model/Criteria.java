package com.ft.report.model;

import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class Criteria {
    private ReportType reportType;
    private String team;
    private Project project;

    public void assignProject(List<Project> projects) {
        Optional<Project> candidate = projects.stream().filter(Project::getPrimary).findFirst();
        if (candidate.isPresent()) {
            project = candidate.get();
        } else {
            project = projects.get(0);
        }
    }

    public void lookupProject(List<Project> projects) {
        Optional<Project> candidate = projects.stream()
                .filter(candidateProject -> candidateProject.getId().equals(this.project.getId()))
                .findFirst();
        if (candidate.isPresent()) {
            this.project = candidate.get();
        }
    }
}
