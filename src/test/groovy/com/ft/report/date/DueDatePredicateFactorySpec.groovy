package com.ft.report.date

import com.ft.report.model.ReportTask
import com.ft.report.model.ReportType
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Clock
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.util.function.Predicate

class DueDatePredicateFactorySpec extends Specification {
    private DueDatePredicateFactory factory

    private static final LocalDate TODAY = LocalDate.of(2015, Month.JUNE, 11)
    private static final LocalDate TOMORROW = TODAY.plusDays(1)
    private static final LocalDate NEXT_SUNDAY = LocalDate.of(2015, Month.JUNE, 14)
    private static final LocalDate NEXT_TUESDAY = LocalDate.of(2015, Month.JUNE, 16)
    private static final LocalDate THIS_WEEK = LocalDate.of(2015, Month.JUNE, 14)
    private static final LocalDate NEXT_WEEK = LocalDate.of(2015, Month.JUNE, 21)

    private static final ZoneId zoneId = ZoneId.systemDefault()

    void setup() {
        factory = new DueDatePredicateFactory()
        factory.clock = Clock.fixed(TODAY.atStartOfDay().atZone(zoneId).toInstant(), zoneId)
    }

    @Unroll
    void "create and verify successful test - reportType: #reportType, dueDate: #dueDate"() {
        given:
            ReportTask shouldPassPredicateReportTask = new ReportTask(due_on: dueDate.toString())

        when:
            Predicate predicate = factory.create(reportType)

        then:
            predicate.test(shouldPassPredicateReportTask)

        where:
            reportType                   | dueDate
            ReportType.SUNDAY_FOR_MONDAY | NEXT_SUNDAY
            ReportType.TODAY             | TODAY
            ReportType.TOMORROW          | TOMORROW
            ReportType.THIS_WEEK         | THIS_WEEK
            ReportType.NEXT_WEEK         | NEXT_WEEK
    }

    @Unroll
    void "create and verify failure test - reportType: #reportType, dueDate: #dueDate"() {
        given:
            ReportTask shouldFailPredicateReportTask = new ReportTask(due_on: dueDate.toString())

        when:
            Predicate predicate = factory.create(reportType)

        then:
            !predicate.test(shouldFailPredicateReportTask)

        where:
            reportType                   | dueDate
            ReportType.SUNDAY_FOR_MONDAY | NEXT_TUESDAY
            ReportType.TODAY             | TOMORROW
            ReportType.TOMORROW          | TODAY
    }
}
