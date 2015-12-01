package com.ft.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Criteria {
    private ReportType reportType;
    private String team;
    private List<Project> projects;
}
