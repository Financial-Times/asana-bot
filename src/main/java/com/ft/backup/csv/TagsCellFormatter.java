package com.ft.backup.csv;

import com.asana.models.Tag;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import java.util.List;
import java.util.stream.Collectors;

public class TagsCellFormatter extends CellProcessorAdaptor implements StringCellProcessor {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);
        try {
            List<Tag> tags = ((List<Tag>) value);
            String formattedValue = tags.stream()
                    .map(tag -> tag.name)
                    .collect(Collectors.joining(", "));

            return next.execute(formattedValue, context);
        } catch (ClassCastException ex) {
            throw new SuperCsvCellProcessorException(List.class, value, context, this);
        }
    }
}
