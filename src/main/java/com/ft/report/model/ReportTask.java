package com.ft.report.model;

import com.ft.asanaapi.model.Tag;
import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

@Data
public class ReportTask {
    private String name;
    private String notes;
    private String due_on;
    private boolean completed = false;
    private List<Tag> tags;
    private List<ReportTask> subtasks;

    public ReportTask() {}

    public boolean isImportant() {
        if (tags == null || tags.isEmpty()) {
            return false;
        }
        return Stream.of(ImportantTag.values()).map(ImportantTag::getValue)
                .anyMatch(importantTag -> tags.stream().anyMatch(tag -> tag.getName().equals(importantTag)));
    }
}
