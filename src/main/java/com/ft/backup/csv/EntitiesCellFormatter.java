package com.ft.backup.csv;

import com.ft.asanaapi.model.AsanaEntity;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import java.util.List;
import java.util.stream.Collectors;

public class EntitiesCellFormatter extends CellProcessorAdaptor implements StringCellProcessor {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);
        try {
            List<? extends AsanaEntity> projects = ((List<? extends AsanaEntity>) value);
            String formattedValue = projects.stream()
                    .map(AsanaEntity::getName)
                    .collect(Collectors.joining(", "));

            return next.execute(formattedValue, context);
        } catch (ClassCastException ex) {
            throw new SuperCsvCellProcessorException(List.class, value, context, this);
        }
    }
}
