package com.ft.report.model;

import com.ft.asanaapi.model.Tag;
import lombok.Data;

import java.util.List;

@Data
public class ReportTask {
    private String name;
    private String notes;
    private String due_on;
    private boolean completed = false;
    private List<Tag> tags;
    private List<ReportTask> subtasks;
}
