package com.ft.report.date

import com.ft.report.model.ReportType
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Clock
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

class ReportDateBuilderSpec extends Specification {

    private static final LocalDateTime MONDAY_MORNING = LocalDateTime.of(2015, Month.JUNE, 1, 10, 0)
    private static final LocalDateTime LAST_FRIDAY_OF_THE_MONTH = LocalDateTime.of(2015, Month.OCTOBER, 30, 10, 0)
    private static final LocalDateTime WEEK_BEFORE_LAST_FRIDAY_OF_THE_MONTH = LAST_FRIDAY_OF_THE_MONTH.minusDays(7)
    private static final ZoneId zoneId = ZoneId.systemDefault()

    ReportDateBuilder reportDateBuilder

    void setup() {
        reportDateBuilder = new ReportDateBuilder()
    }

    @Unroll
    void "build single report date for report type: #reportType"() {
        given:
            reportDateBuilder.clock = Clock.fixed(MONDAY_MORNING.atZone(zoneId).toInstant(), zoneId)

        when:
            String result = reportDateBuilder.buildReportDate(preferredReportType)

        then:
            result == expectedReportDate

        where:
            preferredReportType          | expectedReportDate
            ReportType.TODAY             | '01/06/2015'
            ReportType.TOMORROW          | '02/06/2015'
            ReportType.SUNDAY_FOR_MONDAY | '07/06/2015'
            ReportType.THIS_WEEK         | '06 - 07 June 2015'
            ReportType.NEXT_WEEK         | '13 - 14 June 2015'
    }

    void "build multi report date for report type: #reportType"() {
        given:
            reportDateBuilder.clock = Clock.fixed(date.atZone(zoneId).toInstant(), zoneId)

        when:
            String result = reportDateBuilder.buildReportDate(preferredReportType)

        then:
            result == expectedReportDate

        where:
            preferredReportType  | date                                 | expectedReportDate
            ReportType.THIS_WEEK | LAST_FRIDAY_OF_THE_MONTH             | '31 October - 01 November 2015'
            ReportType.NEXT_WEEK | WEEK_BEFORE_LAST_FRIDAY_OF_THE_MONTH | '31 October - 01 November 2015'
    }
}
