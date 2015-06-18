package com.ft.report.model

import com.ft.asanaapi.model.Tag
import spock.lang.Specification

class ReportTaskSpec extends Specification {

    void "isImportant with no tags"() {
        expect:
            !new ReportTask().important
    }

    void "isImportant with not important tag"() {
        given:
            Tag tag = new Tag(name: 'dummy')
        expect:
            !new ReportTask(tags: [tag]).important
    }

    void "isImportant with important tag"() {
        given:
            ImportantTag importantTag = ImportantTag.asList().first()
            Tag tag = new Tag(name: importantTag.value)
        expect:
            new ReportTask(tags: [tag]).important
    }
}
