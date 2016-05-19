package com.ft.backup.csv

import com.asana.models.Project
import org.supercsv.exception.SuperCsvCellProcessorException
import org.supercsv.util.CsvContext
import spock.lang.Specification
import spock.lang.Unroll

class ProjectsCellFormatterSpec extends Specification {
    private static final CsvContext TEST_CONTEXT = new CsvContext(1, 1, 1)

    private ProjectsCellFormatter formatter

    void setup() {
        formatter = new ProjectsCellFormatter()
    }

    void "execute - success"() {
        given:
            String project1Name = 'test project 1'
            Project project1 = new Project(id: 1, name: project1Name)
            String project2Name = 'test project 2'
            Project project2 = new Project(id: 2, name: project2Name)
            List<Project> tags = [project1, project2]
        and:
            String expectedResult = "${project1Name}, ${project2Name}"

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
