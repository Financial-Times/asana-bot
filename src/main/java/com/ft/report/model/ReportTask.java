package com.ft.report.model;

import com.asana.models.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(exclude = "tags")
@ToString(exclude = "tags")
public class ReportTask {
    private String id;
    private String name;
    public String notes;
    public String due_on;
    public boolean completed = false;
    public List<Tag> tags;
    public List<ReportTask> subtasks;
    public boolean important = false;

    public ReportTask() {}

    public void assignImportant() {
        if (tags == null || tags.isEmpty()) {
            important = false;
        }
        important =  Stream.of(ImportantTag.values()).map(ImportantTag::getValue)
                .anyMatch(importantTag -> tags.stream().anyMatch(tag -> tag.name.equals(importantTag)));
    }

    public String getNotes() {
        return StringUtils.replace(this.notes, "\n", "<br/>");
    }

    public static final Comparator<ReportTask> byImportance = (rt1, rt2) -> Boolean.compare(rt2.isImportant(), rt1.isImportant());
}
