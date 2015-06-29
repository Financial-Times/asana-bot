package com.ft.backup.csv

import com.ft.asanaapi.model.Tag
import org.supercsv.exception.SuperCsvCellProcessorException
import org.supercsv.util.CsvContext
import spock.lang.Specification
import spock.lang.Unroll

class EntitiesCellFormatterSpec extends Specification {

    private static final CsvContext TEST_CONTEXT = new CsvContext(1, 1, 1)

    private EntitiesCellFormatter formatter

    void setup() {
        formatter = new EntitiesCellFormatter()
    }

    void "execute - success"() {
        given:
            String tag1Name = 'test project 1'
            Tag tag1 = new Tag(id: 1, name: tag1Name)
            String tag2Name = 'test project 2'
            Tag tag2 = new Tag(id: 2, name: tag2Name)
            List<Tag> tags = [tag1, tag2]
        and:
            String expectedResult = "${tag1Name}, ${tag2Name}"

        expect:
            expectedResult == formatter.execute(tags, TEST_CONTEXT)
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
            'incompatible value' | 'dummy' | 'the input value should be of type java.util.List but is java.lang.String'
    }
}
