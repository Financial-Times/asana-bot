package com.ft.report.model

import com.asana.models.Tag
import spock.lang.Specification

import java.time.LocalDate
import java.util.stream.Collectors

class ReportTaskSpec extends Specification {

    public static final Tag IMPORTANT_TAG = new Tag(name: ImportantTag.asList().first().value)
    public static final Tag NOT_IMPORTANT_TAG = new Tag(name: 'not important')

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

    void 'sort collection of report tasks by importance, name and date'() {
        given:
            ReportTask importantReportTask = new ReportTask(tags: [IMPORTANT_TAG])
            importantReportTask.assignImportant()

            ReportTask notImportantReportTask = new ReportTask(tags: [NOT_IMPORTANT_TAG])
            notImportantReportTask.assignImportant()

            ReportTask notTaggedReportTask = new ReportTask()
            notImportantReportTask.assignImportant()

            ReportTask dueTask_1 = new ReportTask(due_on: LocalDate.now().minusDays(1).toString())
            notImportantReportTask.assignImportant()

            ReportTask dueTask_2 = new ReportTask(due_on: LocalDate.now().toString())
            notImportantReportTask.assignImportant()

            List<ReportTask> reportTasks = [notTaggedReportTask, importantReportTask, notImportantReportTask,dueTask_1, dueTask_2]
            List<ReportTask> expectedSortedReportTasks = [importantReportTask, notTaggedReportTask, notImportantReportTask,dueTask_1,dueTask_2]

        when:
            List<ReportTask> result = reportTasks.stream()
                    .sorted(ReportTask.byImportance
                                    .thenComparing(ReportTask.byDueDate))
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
