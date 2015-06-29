package com.ft.backup.csv

import org.supercsv.exception.SuperCsvCellProcessorException
import org.supercsv.util.CsvContext
import spock.lang.Specification
import spock.lang.Unroll

class DateTimeCellFormatterSpec extends Specification {

    private static final CsvContext TEST_CONTEXT = new CsvContext(1, 1, 1)

    private DateTimeCellFormatter formatter

    void setup() {
        formatter = new DateTimeCellFormatter()
    }

    void "execute - success"() {
        given:
            String date = '2015-06-25T14:15:30Z'
            String expectedResult = '2015-06-25 14:15:30'

        expect:
            expectedResult == formatter.execute(date, TEST_CONTEXT)
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
            scenario               | value              | message
            'null value'           | null               | 'this processor does not accept null input - if the column is optional then chain an Optional() processor before this one'
            'incompatible value'   | 123456             | 'the input value should be of type java.lang.String but is java.lang.Integer'
            'non ISO format value' | '2015/06/25 14:15' | "Text '2015/06/25 14:15' could not be parsed at index 4"
    }
}
