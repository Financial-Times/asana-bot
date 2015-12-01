package com.ft.report.model;

import lombok.Data;

import java.util.List;

@Data
public class Desk {
    private List<Project> projects;
    private boolean groupTags = false;
    private List<String> premiumTags;
    private boolean showProjects = false;
    private ReportCategory reportCategory = ReportCategory.WEEKDAY;
}
