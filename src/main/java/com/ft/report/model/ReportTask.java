package com.ft.report.model;

import com.asana.models.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.nullsLast;

@Data
@EqualsAndHashCode(exclude = "tags")
@ToString(exclude = "tags")
public class ReportTask {
    private String id;
    private String name;
    public String notes;
    public String due_on;
    public String due_at;
    public boolean completed = false;
    public List<Tag> tags;
    public List<ReportTask> subtasks;
    public boolean important = false;
    private List<CustomField> custom_fields;

    public ReportTask() {
    }

    public void assignImportant() {
        if (tags == null || tags.isEmpty()) {
            important = false;
        } else {
            important = Stream.of(ImportantTag.values()).map(ImportantTag::getValue)
                    .anyMatch(importantTag -> tags.stream().anyMatch(tag -> tag.name.equals(importantTag)));
        }
    }

    public String getNotes() {
        return StringUtils.replace(this.notes, "\n", "<br/>");
    }
    public static final Comparator<ReportTask> byImportance = (rt1, rt2) -> Boolean.compare(rt2.isImportant(), rt1.isImportant());
    public static final Comparator<ReportTask> byDueDate = nullsLast((rt1, rt2) -> rt1.getDateToUse().compareTo(rt2.getDateToUse()));

    public Date getDateToUse() {
        Instant dateInstant;
        if (due_at != null) {
            TemporalAccessor localDateTime = DateTimeFormatter.ISO_INSTANT.parse(due_at);
            dateInstant = Instant.from(localDateTime);
        } else if (due_on != null) {
            TemporalAccessor localDateTime = DateTimeFormatter.ISO_LOCAL_DATE.parse(due_on);
            dateInstant = LocalDate.from(localDateTime).atTime(23, 59).toInstant(ZoneOffset.UTC);
        } else {
            dateInstant = Instant.EPOCH;
        }

        LocalDateTime date = LocalDateTime.ofInstant(dateInstant, ZoneOffset.UTC);
        return Timestamp.valueOf(date);

    }
}
