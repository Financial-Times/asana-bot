package com.ft.backup.csv;

import com.ft.backup.model.BackupTask;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class BackupTaskCellFormater extends CellProcessorAdaptor implements StringCellProcessor {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);
        validateInputClass(value, context);

        String formattedValue = formatValue(value);
        return next.execute(formattedValue, context);
    }

    private String formatValue(Object value) {
        BackupTask backupTask = (BackupTask) value;
        return backupTask.getName();
    }

    private void validateInputClass(Object value, CsvContext context) {
        if (!(value instanceof BackupTask)) {
            throw new SuperCsvCellProcessorException(BackupTask.class, value, context, this);
        }
    }
}
