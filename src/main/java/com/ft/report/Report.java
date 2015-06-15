package com.ft.report;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Report {

    private Map<String, List<ReportTask>> tagTasks;
}
