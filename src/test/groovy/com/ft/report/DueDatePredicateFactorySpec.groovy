package com.ft.report

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
    private static final LocalDate NEXT_MONDAY = LocalDate.of(2015, Month.JUNE, 15)
    private static final LocalDate NEXT_TUESDAY = LocalDate.of(2015, Month.JUNE, 16)

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
            ReportType.SUNDAY_FOR_MONDAY | NEXT_MONDAY
            ReportType.TODAY             | TODAY
            ReportType.TOMORROW          | TOMORROW
            null                         | null
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
