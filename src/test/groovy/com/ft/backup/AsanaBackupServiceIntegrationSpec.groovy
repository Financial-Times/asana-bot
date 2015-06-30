package com.ft.backup

import com.ft.backup.drive.GoogleDriveService
import com.ft.test.IntegrationSpec
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import org.springframework.beans.factory.annotation.Autowired

import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId

import static com.github.tomakehurst.wiremock.client.WireMock.*

class AsanaBackupServiceIntegrationSpec extends IntegrationSpec {

    private static final String ENCODED_OPT_EXPAND = "%28this%7Csubtasks%2B%29"
    private static final String DECODED_OPT_EXPAND = "(this|subtasks+)"
    private static final String TEST_PICTURES_PROJECT_ID = "36788370362617"
    private static final String TEST_UK_PROJECT_ID = "37532256694667"

    private static final LocalDateTime WEEK_AHEAD = LocalDateTime.now().plusDays(7).plusHours(1)
    private static final ZoneId zoneId = ZoneId.systemDefault()

    @Autowired AsanaBackupService asanaBackupService
    @Autowired GoogleDriveService googleDriveService

    void setup() {
        asanaBackupService.clock = Clock.fixed(WEEK_AHEAD.atZone(zoneId).toInstant(), zoneId)
    }

    void "backupAllProjects and remove old backup files"() {
        given:
            stubGetProjects()
            stubGetProjectTasks(TEST_PICTURES_PROJECT_ID)
            stubGetProjectTasks(TEST_UK_PROJECT_ID)

        when:
            asanaBackupService.backupAllProjects()

        then:
            verifyGetProjects()
            verifyGetProjectTasks(TEST_PICTURES_PROJECT_ID)
            verifyGetProjectTasks(TEST_UK_PROJECT_ID)

        and:
            getTestFiles().size() >= 2

        when:
            asanaBackupService.removeOldBackupFiles()

        then: 'only root folder remains'
            List<File> files = getTestFiles()
            files.size() >= 1

        cleanup:
            listAndDeleteMyFiles(files)
    }

    private void stubGetProjects() {
        wireMockRule.stubFor(get(urlMatching("/api/1.0/projects"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("backup/projects.json")))
    }

    private void stubGetProjectTasks(String projectId) {
        wireMockRule.stubFor(get(urlMatching("/api/1.0/projects/${projectId}/tasks\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("opt_expand", equalTo(ENCODED_OPT_EXPAND))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("backup/tasks-${projectId}.json")))
    }

    private boolean verifyGetProjects() {
        wireMockRule.verify(1, getRequestedFor(urlMatching("/api/1.0/projects"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER)))
        return true
    }

    private boolean verifyGetProjectTasks(String projectId) {
        wireMockRule.verify(1, getRequestedFor(urlMatching("/api/1.0/projects/" + projectId + "/tasks\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("opt_expand", equalTo(DECODED_OPT_EXPAND))
                )
        return true
    }

    private List<File> getTestFiles() throws IOException {
        FileList result = googleDriveService.drive.files().list()
                .setQ("title contains 'Test'")
                .setMaxResults(50)
                .execute()
        return result.getItems()
    }

    private void listAndDeleteMyFiles(List<File> files) throws IOException {
        files.each { file ->
            println file.title
            try {
                googleDriveService.drive.files().delete(file.getId()).execute()
            } catch (Exception ignore) {
                println "Didn't delete the file due to ${ignore.message}"
            }
        }
    }
}