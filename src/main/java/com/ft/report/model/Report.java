package com.ft.report.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Report {

    private boolean groupByTags = false;
    private Project project;
    private Map<String, List<ReportTask>> tagTasks;
}
