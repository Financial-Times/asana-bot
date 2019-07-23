package com.ft.report

import com.ft.report.date.ReportDateBuilder
import com.ft.report.model.*
import com.ft.services.EmailService
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.OAuth2Authentication
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
    private ReportDateBuilder mockReportDateBuilder
    private ReportGenerator mockReportGenerator
    private EmailService mockEmailService

    void setup() {
        controller = new ReportsController()

        mockReportDateBuilder = Mock(ReportDateBuilder)
        controller.reportDateBuilder = mockReportDateBuilder

        mockReportGenerator = Mock(ReportGenerator)
        controller.reportGenerator = mockReportGenerator

        mockEmailService = Mock(EmailService)
        controller.emailService = mockEmailService

        Project project1 = new Project(id: 1, name: 'project 1')
        Project project2 = new Project(id: 2, name: 'project 2')
        Desk desk1 = new Desk(projects: [project1])
        Desk desk2 = new Desk(projects: [project2])
        controller.desks = ['one': desk1, 'two': desk2]
    }

    void 'populate report types'() {
        when:
            ReportType[] result = controller.populateReportTypes()
        then:
            result.size() == 7
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
    void "home - #scenario"() {
        given:
            ReportType preferredReportType = ReportType.SUNDAY_FOR_MONDAY
            ReportType weekEndpreferredReportType = ReportType.NEXT_WEEK
            Map<String, Object> model = [:]
            Criteria expectedCriteria = new Criteria(reportType: preferredReportType, team: expectedTeam, projects: [])

        when:
            String viewName = controller.home(teams, preferredReportType, weekEndpreferredReportType, model)

        then:
            viewName == 'reports/home'
            model['criteria'] == expectedCriteria

        where:
            scenario              | teams                  | expectedTeam
            'user with no teams'  | [:]                    | null
            'user with two teams' | ['one': [], 'two': []] | 'one'
    }

    void "create Sunday for Monday report"() {
        given:
            String team = 'one'
        and:
            Project project = new Project(id: 1, name: 'project 1', primary: true)
            Criteria criteria = new Criteria(team: team, reportType: ReportType.SUNDAY_FOR_MONDAY, projects: [project])
            ModelMap modelMap = new ModelMap()
            List<Report> expectedReports = [new Report(project: project)]

        when:
            String viewName = controller.createMultiProject(criteria, false, modelMap)

        then:
            1 * _.generate(criteria) >> expectedReports
            1 * mockReportDateBuilder.buildReportDate(ReportType.SUNDAY_FOR_MONDAY)
            0 * _
        and:
            viewName == 'reports/home'
            modelMap['criteria'] == criteria
            modelMap['reports'] == [(project.name): [expectedReports[0]]]
    }

    void "user teams are populated"() {
        given:
            SecurityContextHolder mockSecurityContextHolder = Spy(SecurityContextHolder)
            SecurityContext mockSecurityContext = Mock(SecurityContext)
            mockSecurityContextHolder.context = mockSecurityContext
        and:
            OAuth2Authentication mockOAuth2Authentication = Mock(OAuth2Authentication)
            Authentication mockAuthentication = Mock(Authentication)
            def userDetails = [:]

        when:
            def teams = controller.populateUserDesks()

        then:
            1 * mockSecurityContext.getAuthentication() >> mockOAuth2Authentication
            1 * mockOAuth2Authentication.getUserAuthentication() >> mockAuthentication
            1 * mockAuthentication.getDetails() >> userDetails
            0 * _

        and:
            notThrown(NullPointerException)
            teams == [:]
    }

    void "email link is set to true"() {
        given:
            String team = 'one'

        when:
            def showEmailLink = controller.showEmailLink(team)

        then:
            1 * _.isEmailTeam(team) >> true
            0 * _
        and:
            showEmailLink
    }

    void "send report when email provided"() {
        given:
            String team = 'one'


        and:
            Criteria criteria = new Criteria(team: team, reportType: ReportType.SUNDAY_FOR_MONDAY, projects: [new Project(id: 1, name: 'project 1')])
            ModelMap modelMap = new ModelMap()
            def reports = []

        when:
            controller.createMultiProject(criteria, true, modelMap)

        then:
            1 * mockEmailService.sendEmail(team, _ , reports)
            1 * mockReportGenerator.generate(criteria) >> []
            1 * mockReportDateBuilder.buildReportDate(ReportType.SUNDAY_FOR_MONDAY)
            0 * _
    }

    void "create weekend report"() {
        given:
            String team = 'one'
            Project project = new Project(id: 1, name: 'project 1')
            Criteria criteria = new Criteria(team: team, reportType: ReportType.THIS_WEEK, projects: [project])
            ModelMap modelMap = new ModelMap()
            List<Report> expectedReports = [new Report(project: project)]

        when:
            String viewName = controller.createMultiProject(criteria, false, modelMap)

        then:
            1 * mockReportGenerator.generate(criteria) >> expectedReports
            1 * mockReportDateBuilder.buildReportDate(ReportType.THIS_WEEK)
            0 * _
        and:
            viewName == 'reports/home'
            modelMap['criteria'] == criteria
            modelMap['reports']['project 1'] == [expectedReports[0]]
    }
}
