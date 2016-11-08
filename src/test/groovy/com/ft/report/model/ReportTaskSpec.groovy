package com.ft.report.model

import com.asana.models.Tag
import spock.lang.Specification

import java.time.LocalDate
import java.util.stream.Collectors

class ReportTaskSpec extends Specification {

    public static final Tag IMPORTANT_TAG = new Tag(name: ImportantTag.asList().first().value)

    void "isImportant with no tags"() {
        expect:
            !new ReportTask().important
    }

    void "isImportant with not important tag"() {
        given:
            Tag tag = new Tag(name: 'dummy')
            ReportTask reportTask = new ReportTask(tags: [tag])

        when:
            reportTask.assignImportant()

        then:
            !reportTask.important
    }

    void "isImportant with important tag"() {
        given:
            ReportTask reportTask = new ReportTask(tags: [IMPORTANT_TAG])

        when:
            reportTask.assignImportant()

        then:
            reportTask.important
    }

    void 'sort collection of report tasks by date'() {
        given:
            def today = LocalDate.now().toString()
            def tomorrow = LocalDate.now().plusDays(1).toString()
            def tomorrowTime = (new Date() + 1 ).format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC"))

            ReportTask dueTask_1 = new ReportTask(due_on: today)

            ReportTask dueTask_2 = new ReportTask(due_on: tomorrow)

            ReportTask dueTask_3 = new ReportTask(due_at: tomorrowTime)

            List<ReportTask> reportTasks = [dueTask_1, dueTask_2, dueTask_3]
            List<ReportTask> expectedSortedReportTasks = [dueTask_1, dueTask_3, dueTask_2]

        when:
            List<ReportTask> result = reportTasks.stream()
                    .sorted(ReportTask.byDueDate)
                    .collect(Collectors.toList())
        then:
            result == expectedSortedReportTasks
    }

    void "newline in notes becomes line break"() {
        given:
            ReportTask reportTask = new ReportTask(notes: "note\n" +
                    "with\n" +
                    "newline")

        expect:
            reportTask.notes == "note<br/>with<br/>newline"
    }
}
