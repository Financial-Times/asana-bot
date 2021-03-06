package com.ft.backup.csv;

import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.*;

@Component
public class CsvTemplate {

    private static final NotNull NOT_NULL_CELL_PROCESSOR = new NotNull();
    private static final Optional OPTIONAL_CELL_PROCESSOR = new Optional();
    private static final FmtBool BOOLEAN_CELL_FORMATTER = new FmtBool("Yes", "No");
    private static final Optional USER_CELL_FORMATTER = new Optional(new UserCellFormatter());
    private static final Optional PARENT_TASK_CELL_FORMATTER = new Optional(new BackupTaskCellFormater());
    private static final Optional PROJECTS_CELL_FORMATTER = new Optional(new ProjectsCellFormatter());
    private static final Optional TAGS_CELL_FORMATTER = new Optional(new TagsCellFormatter());
    private static final Optional DATE_TIME_CELL_FORMATTER = new Optional(new DateTimeCellFormatter());

    private static final Map<String, CellProcessor> mapping = initMapping();

    private static Map<String, CellProcessor> initMapping() {
        HashMap<String, CellProcessor> mapping = new LinkedHashMap<>();
        mapping.put("projects", PROJECTS_CELL_FORMATTER);
        mapping.put("gid", NOT_NULL_CELL_PROCESSOR);
        mapping.put("name", OPTIONAL_CELL_PROCESSOR);
        mapping.put("parent", PARENT_TASK_CELL_FORMATTER);
        mapping.put("notes", OPTIONAL_CELL_PROCESSOR);
        mapping.put("createdAt", DATE_TIME_CELL_FORMATTER);
        mapping.put("modifiedAt", DATE_TIME_CELL_FORMATTER);
        mapping.put("completed", new Optional(BOOLEAN_CELL_FORMATTER));
        mapping.put("completedAt", DATE_TIME_CELL_FORMATTER);
        mapping.put("assignee", USER_CELL_FORMATTER);
        mapping.put("dueOn", OPTIONAL_CELL_PROCESSOR);
        mapping.put("tags", TAGS_CELL_FORMATTER);
        mapping.put("notes", OPTIONAL_CELL_PROCESSOR);
        return mapping;
    }

    public String[] getHeaders() {
        Set<String> headers = mapping.keySet();
        return headers.toArray(new String[headers.size()]);
    }

    public CellProcessor[] getProcessors() {
        Collection<CellProcessor> values = mapping.values();
        return values.toArray(new CellProcessor[values.size()]);
    }
}
