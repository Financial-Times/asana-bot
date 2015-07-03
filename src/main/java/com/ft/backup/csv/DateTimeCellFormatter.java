package com.ft.backup.csv;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeCellFormatter extends CellProcessorAdaptor implements StringCellProcessor {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);
        try {
            return formatDate((String) value, context);
        } catch (ClassCastException ex) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        } catch (DateTimeParseException ex) {
            throw new SuperCsvCellProcessorException(ex.getMessage(), context, this);
        }
    }

    private Object formatDate(String value, CsvContext context) {
        Instant date = Instant.parse(value);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date, UTC_ZONE_ID);
        String formattedValue = localDateTime.format(formatter);
        return next.execute(formattedValue, context);
    }
}
