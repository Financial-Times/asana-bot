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
    private static final Optional NAME_CELL_FORMATTER = new Optional(new EntityCellFormatter());
    private static final Optional NAMES_CELL_FORMATTER = new Optional(new EntitiesCellFormatter());
    private static final Optional DATE_TIME_CELL_FORMATTER = new Optional(new DateTimeCellFormatter());

    private static final Map<String, CellProcessor> mapping = initMapping();

    private static Map<String, CellProcessor> initMapping() {
        HashMap<String, CellProcessor> mapping = new LinkedHashMap<>();
        mapping.put("projects", NAMES_CELL_FORMATTER);
        mapping.put("id", NOT_NULL_CELL_PROCESSOR);
        mapping.put("name", OPTIONAL_CELL_PROCESSOR);
        mapping.put("parent", NAME_CELL_FORMATTER);
        mapping.put("notes", OPTIONAL_CELL_PROCESSOR);
        mapping.put("created_at", DATE_TIME_CELL_FORMATTER);
        mapping.put("modified_at", DATE_TIME_CELL_FORMATTER);
        mapping.put("completed", new Optional(BOOLEAN_CELL_FORMATTER));
        mapping.put("completed_at", DATE_TIME_CELL_FORMATTER);
        mapping.put("assignee", NAME_CELL_FORMATTER);
        mapping.put("due_date", OPTIONAL_CELL_PROCESSOR);
        mapping.put("tags", NAMES_CELL_FORMATTER);
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
