package com.ft.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Criteria {
    private ReportType reportType;
    private String team;
    private List<Project> projects;

    public List<String> getProjectIds(){
        return projects.stream().map(Project::getId).collect(Collectors.toList());
    }
}
