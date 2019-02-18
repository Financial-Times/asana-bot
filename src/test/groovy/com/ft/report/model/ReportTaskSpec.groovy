package com.ft.report.model

import com.asana.models.Tag
import com.ft.asanaapi.model.CustomTask
import spock.lang.Specification

import java.time.LocalDate
import java.util.stream.Collectors

class ReportTaskSpec extends Specification {

    public static final Tag IMPORTANT_TAG = new Tag(name: ImportantTag.asList().first().value)
    public static final Tag NOT_IMPORTANT_TAG = new Tag(name: 'not important')

    void "isImportant with no tags"() {
        expect:
            !new CustomTask().important
    }

    void "isImportant with not important tag"() {
        given:
            Tag tag = new Tag(name: 'dummy')
            CustomTask reportTask = new CustomTask(tags: [tag])

        when:
            reportTask.assignImportant()

        then:
            !reportTask.important
    }

    void "isImportant with important tag"() {
        given:
            CustomTask reportTask = new CustomTask(tags: [IMPORTANT_TAG])

        when:
            reportTask.assignImportant()

        then:
            reportTask.important
    }

    void 'sort collection of report tasks by importance, name and date'() {
        given:
            CustomTask importantReportTask = new CustomTask(tags: [IMPORTANT_TAG])
            importantReportTask.assignImportant()

            CustomTask notImportantReportTask = new CustomTask(tags: [NOT_IMPORTANT_TAG])
            notImportantReportTask.assignImportant()

            CustomTask notTaggedReportTask = new CustomTask()
            notImportantReportTask.assignImportant()

            CustomTask dueTask_1 = new CustomTask(due_on: LocalDate.now().minusDays(1).toString())
            notImportantReportTask.assignImportant()

            CustomTask dueTask_2 = new CustomTask(due_on: LocalDate.now().toString())
            notImportantReportTask.assignImportant()

            List<CustomTask> reportTasks = [notTaggedReportTask, importantReportTask, notImportantReportTask, dueTask_1, dueTask_2]
            List<CustomTask> expectedSortedReportTasks = [importantReportTask, notTaggedReportTask, notImportantReportTask, dueTask_1, dueTask_2]

        when:
            List<CustomTask> result = reportTasks.stream()
                    .sorted(CustomTask.byImportance
                                    .thenComparing(CustomTask.byDueDate))
                    .collect(Collectors.toList())
        then:
            result == expectedSortedReportTasks
    }

    void "newline in notes becomes line break"() {
        given:
            CustomTask reportTask = new CustomTask(notes: "note\n" +
                    "with\n" +
                    "newline")

        expect:
            reportTask.notes == "note<br/>with<br/>newline"
    }
}
