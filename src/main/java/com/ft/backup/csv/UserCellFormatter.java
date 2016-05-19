package com.ft.backup.csv;

import com.asana.models.User;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class UserCellFormatter extends CellProcessorAdaptor implements StringCellProcessor {
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);
        validateInputClass(value, context);

        String formattedValue = formatValue(value);
        return next.execute(formattedValue, context);
    }

    private String formatValue(Object value) {
        User user = (User) value;
        return user.name;
    }

    private void validateInputClass(Object value, CsvContext context) {
        if (!(value instanceof User)) {
            throw new SuperCsvCellProcessorException(User.class, value, context, this);
        }
    }
}
