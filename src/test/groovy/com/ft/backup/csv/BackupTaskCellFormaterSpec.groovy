package com.ft.backup.csv

import com.ft.backup.model.BackupTask
import org.supercsv.exception.SuperCsvCellProcessorException
import org.supercsv.util.CsvContext
import spock.lang.Specification
import spock.lang.Unroll

class BackupTaskCellFormaterSpec extends Specification {
    private static final CsvContext TEST_CONTEXT = new CsvContext(1, 1, 1)

    private BackupTaskCellFormater formatter

    void setup() {
        formatter = new BackupTaskCellFormater()
    }

    void "execute - success"() {
        given:
            String expectedResult = 'test task'
            BackupTask value = new BackupTask(id: 1, name: expectedResult)

        expect:
            expectedResult == formatter.execute(value, TEST_CONTEXT)
    }

    @Unroll
    void "execute - failure due to #scenario"() {
        when:
            formatter.execute(value, TEST_CONTEXT)

        then:
            Exception caught = thrown()
            caught instanceof SuperCsvCellProcessorException
            caught.message == message

        where:
            scenario             | value   | message
            'null value'         | null    | 'this processor does not accept null input - if the column is optional then chain an Optional() processor before this one'
            'incompatible value' | 'dummy' | 'the input value should be of type com.ft.backup.model.BackupTask but is java.lang.String'
    }

}
