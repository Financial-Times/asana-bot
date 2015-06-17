package com.ft.report.model

import spock.lang.Specification
import spock.lang.Unroll

class ReportTypeSpec extends Specification {

    @Unroll
    void "format - #reportType"() {
        expect:
            reportType.format() == expectedFormattedReportType

        where:
            reportType | expectedFormattedReportType
            ReportType.SUNDAY_FOR_MONDAY | 'Sunday For Monday'
            ReportType.TODAY | 'Today'
            ReportType.TOMORROW | 'Tomorrow'
    }
}
