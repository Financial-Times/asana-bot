package com.ft.report

import com.ft.report.model.Criteria
import com.ft.report.model.Report
import com.ft.report.model.ReportType
import org.springframework.ui.ModelMap
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Clock
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

class ReportsControllerSpec extends Specification {
    private static final LocalDateTime FRIDAY_EVENING = LocalDateTime.of(2015, Month.JUNE, 5, 15, 0)
    private static final LocalDateTime MONDAY_EVENING = LocalDateTime.of(2015, Month.JUNE, 1, 15, 0)
    private static final LocalDateTime MONDAY_MORNING = LocalDateTime.of(2015, Month.JUNE, 1, 10, 0)
    private static final ZoneId zoneId = ZoneId.systemDefault()

    private ReportsController controller

    void setup() {
        controller = new ReportsController()
    }

    void 'populate report types'() {
        when:
            ReportType[] result = controller.populateReportTypes()
        then:
            result.size() == 3
            result == ReportType.values()
    }

    @Unroll
    void "populatePreferredReportType - date: #date"() {
        given:
            controller.clock = Clock.fixed(date.atZone(zoneId).toInstant(), zoneId)

        when:
            ReportType result = controller.populatePreferredReportType()

        then:
            result == expectedResult

        where:
            date           | expectedResult
            MONDAY_MORNING | ReportType.TODAY
            MONDAY_EVENING | ReportType.TOMORROW
            FRIDAY_EVENING | ReportType.SUNDAY_FOR_MONDAY
    }


    @Unroll
    void "buildReportDate for report type: #reportType"() {
        given:
            controller.clock = Clock.fixed(MONDAY_MORNING.atZone(zoneId).toInstant(), zoneId)

        when:
            String result = controller.buildReportDate(preferredReportType)

        then:
            result == expectedReportDate

        where:
            preferredReportType          | expectedReportDate
            ReportType.TODAY             | '01/06/2015'
            ReportType.TOMORROW          | '02/06/2015'
            ReportType.SUNDAY_FOR_MONDAY | '07/06/2015'
    }

    @Unroll
    void "home - #scenario"() {
        given:
            ReportType preferredReportType = ReportType.SUNDAY_FOR_MONDAY
            Map<String, Object> model = [:]
            Criteria expectedCriteria = new Criteria(reportType: preferredReportType, team: expectedTeam)

        when:
            String viewName = controller.home(teams, preferredReportType, model)

        then:
            viewName == 'reports/home'
            model['criteria'] == expectedCriteria

        where:
            scenario              | teams          | expectedTeam
            'user with no teams'  | []             | null
            'user with two teams' | ['one', 'two'] | 'one'
    }

    void "create Sunday for Monday report"() {
        given:
            String team = 'test team'
            ReportGenerator mockDefaultReportGenerator = Mock(ReportGenerator)
            controller.reportGenerator = mockDefaultReportGenerator
        and:
            Criteria criteria = new Criteria(team: team, reportType: ReportType.SUNDAY_FOR_MONDAY)
            ModelMap modelMap = new ModelMap()
            Report expectedReport = new Report()

        when:
            String viewName = controller.create(criteria, modelMap)

        then:
            1 * mockDefaultReportGenerator.generate(criteria.reportType, team) >> expectedReport
            0 * _
        and:
            viewName == 'reports/home'
            modelMap['criteria'] == criteria
            modelMap['report'] == expectedReport
    }
}
