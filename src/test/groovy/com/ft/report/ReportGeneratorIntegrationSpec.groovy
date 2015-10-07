package com.ft.report

import com.ft.asanaapi.model.Tag
import com.ft.report.model.Criteria
import com.ft.report.model.Project
import com.ft.report.model.Report
import com.ft.report.model.ReportTask
import com.ft.report.model.ReportType
import com.ft.test.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired

import java.time.Clock
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.containing
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo

class ReportGeneratorIntegrationSpec extends IntegrationSpec {

    private static final Long TEST_COMPANIES_PROJECT_ID = 12345L
    private static final Long TEST_WORLD_PROJECT_ID = 23456L
    private static final Long TEST_LEX_PROJECT_ID = 9876L
    private static final Long TEST_BIG_READ_PROJECT_1_ID = 100048121L
    private static final Long TEST_BIG_READ_PROJECT_2_ID = 100048122L
    private static final String encodedOptFields = "name%2Ctags.name%2Cdue_on%2Cnotes%2Ccompleted%2Csubtasks.name%2Csubtasks.completed"
    private static final String decodedOptFields = "name,tags.name,due_on,notes,completed,subtasks.name,subtasks.completed"

    private static final LocalDateTime FRIDAY_EVENING = LocalDateTime.of(2015, Month.JUNE, 12, 15, 0)
    private static final ZoneId zoneId = ZoneId.systemDefault()
    private static final String OTHER_TAG = 'Others'
    private static final String NOT_TAGGED = 'Not tagged'

    @Autowired ReportGenerator generator
    @Autowired DueDatePredicateFactory dueDatePredicateFactory

    void setup() {
        dueDatePredicateFactory.clock = Clock.fixed(FRIDAY_EVENING.atZone(zoneId).toInstant(), zoneId)
    }

    void "generate report with premium tags and grouping, e.g. companies sunday for monday report"() {
        given:
            String team = 'Companies'
            stubGetTasks(team, TEST_COMPANIES_PROJECT_ID)
            List<ReportTask> expectedFinservTasks = createFinservTasks()
            List<ReportTask> expectedNotTaggedTasks = createNotTaggedTask()
            List<ReportTask> expectedOtherTasks = createOtherTask()
            Criteria criteria = new Criteria(reportType: ReportType.SUNDAY_FOR_MONDAY, team: team, project: new Project(id: TEST_COMPANIES_PROJECT_ID))

        when:
            Report report = generator.generate(criteria)

        then:
            verifyGetTasks(TEST_COMPANIES_PROJECT_ID)
        and:
            report
            report.tagTasks.size() == 3
            report.tagTasks['Finserv'] == expectedFinservTasks
            report.tagTasks[OTHER_TAG].size() == 1
            report.tagTasks[OTHER_TAG] == expectedOtherTasks
            report.tagTasks[NOT_TAGGED].size() == 1
            report.tagTasks[NOT_TAGGED] == expectedNotTaggedTasks
    }

    void "generate report without premium tags but with grouping, e.g. world sunday for monday report"() {
        given:
            String team = 'World'
            stubGetTasks(team, TEST_WORLD_PROJECT_ID)
            List<ReportTask> expectedEuropeTasks = createEuropeTasks()
            Criteria criteria = new Criteria(reportType: ReportType.SUNDAY_FOR_MONDAY, team: team, project: new Project(id: TEST_WORLD_PROJECT_ID))

        when:
            Report report = generator.generate(criteria)

        then:
            verifyGetTasks(TEST_WORLD_PROJECT_ID)
        and:
            report
            report.tagTasks.size() == 1
            report.tagTasks['Europe'].size() == 3
            report.tagTasks['Europe'] == expectedEuropeTasks
    }

    void "generate report without premium tags neither with grouping, e.g. Lex sunday for monday report"() {
        given:
            String team = 'Lex'
            stubGetTasks(team, TEST_LEX_PROJECT_ID)
            List<ReportTask> expectedLexTasks = createLexTasks()
            Criteria criteria = new Criteria(reportType: ReportType.SUNDAY_FOR_MONDAY, team: team, project: new Project(id: TEST_LEX_PROJECT_ID))

        when:
            Report report = generator.generate(criteria)

        then:
            verifyGetTasks(TEST_LEX_PROJECT_ID)
        and:
            report
            report.tagTasks.size() == 1
            report.tagTasks[NOT_TAGGED].size() == 2
            report.tagTasks[NOT_TAGGED] == expectedLexTasks
    }

    void "generate project report for a multi project team without premium tags neither with grouping, e.g. Big Read sunday for monday report"() {
        given:
            String team = 'Big Read'
            stubGetTasks(team, TEST_BIG_READ_PROJECT_1_ID)
            List<ReportTask> expectedBigReadTasks = createBigReadTasks()
            Criteria criteria = new Criteria(reportType: ReportType.SUNDAY_FOR_MONDAY, team: team, project: new Project(id: TEST_BIG_READ_PROJECT_1_ID))

        when:
            Report report = generator.generate(criteria)

        then:
            verifyGetTasks(TEST_BIG_READ_PROJECT_1_ID)
        and:
            report
            report.tagTasks.size() == 1
            report.tagTasks[NOT_TAGGED].size() == 2
            report.tagTasks[NOT_TAGGED] == expectedBigReadTasks
    }

    private static List<ReportTask> createFinservTasks() {
        ReportTask reportTask = new ReportTask()
        reportTask.name = "Finserv task 1"
        reportTask.notes = "some notes"
        reportTask.completed = false
        reportTask.due_on = "2015-06-14"
        reportTask.subtasks = []
        reportTask.tags = [new Tag(id: 33751312101034, name: "Asia")]

        ReportTask importantReportTask = new ReportTask()
        importantReportTask.name = "Finserv task 2"
        importantReportTask.notes = "some notes"
        importantReportTask.completed = false
        importantReportTask.due_on = "2015-06-14"
        importantReportTask.subtasks = []
        importantReportTask.important = true
        importantReportTask.tags = [new Tag(id: 33751312101134, name: "Level 1")]
        return [importantReportTask, reportTask]
    }

    private static List<ReportTask> createNotTaggedTask() {
        ReportTask reportTask1 = new ReportTask()
        reportTask1.name = "Test task 1"
        reportTask1.notes = ""
        reportTask1.completed = false
        reportTask1.due_on = "2015-06-14"
        reportTask1.tags = []
        reportTask1.subtasks = [new ReportTask(name: "pictures for test task 1", completed: true), new ReportTask(name: "graphics for test task 1", completed: false)]

        return [reportTask1] as List<ReportTask>
    }

    private static List<ReportTask> createEuropeTasks() {
        ReportTask reportTask1 = new ReportTask()
        reportTask1.id = '37354116382321'
        reportTask1.name = "Europe task 1"
        reportTask1.notes = "some notes"
        reportTask1.completed = false
        reportTask1.due_on = "2015-06-14"
        reportTask1.tags = [new Tag(id: '33751312101034', name: 'Asia')]
        reportTask1.subtasks = []

        ReportTask reportTask2 = new ReportTask()
        reportTask2.id = '37354116382322'
        reportTask2.name = "Europe task 2"
        reportTask2.notes = "some notes"
        reportTask2.completed = false
        reportTask2.due_on = "2015-06-14"
        reportTask2.tags = []
        reportTask2.subtasks = []

        ReportTask reportTask3 = new ReportTask()
        reportTask3.id = '37354116382323'
        reportTask3.name = "Important Europe task 3"
        reportTask3.important = true
        reportTask3.notes = "some important notes"
        reportTask3.completed = false
        reportTask3.due_on = "2015-06-14"
        reportTask3.tags = [new Tag(id: '33751312101134', name: 'Level 1')]
        reportTask3.subtasks = []

        return [reportTask3, reportTask1, reportTask2] as List<ReportTask>
    }

    private static List<ReportTask> createLexTasks() {
        Tag lawTag = new Tag(id: '32896507462027', name: 'Law')
        ReportTask reportTask1 = new ReportTask()
        reportTask1.id = '37354116382321'
        reportTask1.name = "Lex task 1"
        reportTask1.notes = "some notes"
        reportTask1.completed = false
        reportTask1.due_on = "2015-06-14"
        reportTask1.tags = [lawTag, new Tag(id: '33751312101034', name: 'Justice')]
        reportTask1.subtasks = []

        ReportTask reportTask2 = new ReportTask()
        reportTask2.id = '37354116382323'
        reportTask2.name = "Important Lex task 2"
        reportTask2.important = true
        reportTask2.notes = "some important notes"
        reportTask2.completed = false
        reportTask2.due_on = "2015-06-14"
        reportTask2.tags = [lawTag, new Tag(id: '33751312101134', name: 'Level 1')]
        reportTask2.subtasks = []

        return [reportTask2, reportTask1] as List<ReportTask>
    }

    private static List<ReportTask> createBigReadTasks() {
        Tag bigTag = new Tag(id: '32896507462037', name: 'Big')
        Tag readTag = new Tag(id: '33751312101038', name: 'Read')
        Tag level1Tag = new Tag(id: '33751312101134', name: 'Level 1')

        ReportTask reportTask1 = new ReportTask()
        reportTask1.id = '37354116382321'
        reportTask1.name = "Big read project 1 task 1"
        reportTask1.notes = "some notes"
        reportTask1.completed = false
        reportTask1.due_on = "2015-06-14"
        reportTask1.tags = [bigTag, readTag]
        reportTask1.subtasks = []

        ReportTask reportTask2 = new ReportTask()
        reportTask2.id = '37354116382323'
        reportTask2.name = "Important Big Read project 1 task 2"
        reportTask2.important = true
        reportTask2.notes = "some important notes"
        reportTask2.completed = false
        reportTask2.due_on = "2015-06-14"
        reportTask2.tags = [bigTag, level1Tag]
        reportTask2.subtasks = []

        return [reportTask2, reportTask1] as List<ReportTask>
    }

    private static List<ReportTask> createOtherTask() {
        ReportTask reportTask2 = new ReportTask()
        reportTask2.name = "Other task 1"
        reportTask2.notes = "some notes"
        reportTask2.completed = false
        reportTask2.due_on = "2015-06-14"
        reportTask2.tags = [new Tag(id: '32896507462011', name: 'Unmapped tag')]
        reportTask2.subtasks = []

        return [reportTask2] as List<ReportTask>
    }

    private stubGetTasks(String desk, Long projectId) {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/1.0/tasks"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("workspace", equalTo(testWorkspaceId))
                .withQueryParam("project", equalTo(projectId.toString()))
                .withQueryParam("completed_since", equalTo("now"))
                .withQueryParam("opt_fields", equalTo(encodedOptFields))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", APPLICATION_JSON_CONTENT_TYPE)
                .withBodyFile("report/${desk.toLowerCase()}-${projectId}_sunday_4_monday.json")))
    }

    private boolean verifyGetTasks(Long projectId) {
        wireMockRule.verify(1, getRequestedFor(urlMatching("/api/1.0/tasks\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("workspace", equalTo(testWorkspaceId))
                .withQueryParam("project", equalTo(projectId.toString()))
                .withQueryParam("completed_since", equalTo("now"))
                .withQueryParam("opt_fields", matching(decodedOptFields)))
        return true
    }
}
