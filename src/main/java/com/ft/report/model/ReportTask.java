package com.ft.report.model;

import com.ft.asanaapi.model.AsanaEntity;
import com.ft.asanaapi.model.Tag;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ReportTask extends AsanaEntity {
    private String notes;
    private String due_on;
    private boolean completed = false;
    private List<Tag> tags;
    private List<ReportTask> subtasks;
    private boolean important = false;

    public ReportTask() {}

    public void assignImportant() {
        if (tags == null || tags.isEmpty()) {
            important = false;
        }
        important =  Stream.of(ImportantTag.values()).map(ImportantTag::getValue)
                .anyMatch(importantTag -> tags.stream().anyMatch(tag -> tag.getName().equals(importantTag)));
    }

    public String getNotes() {
        return StringUtils.replace(this.notes, "\n", "<br/>");
    }

    public static final Comparator<ReportTask> byImportance = (rt1, rt2) -> Boolean.compare(rt2.isImportant(), rt1.isImportant());
}
