package com.ft.services

import com.ft.backup.AsanaBackupService
import com.ft.monitoring.AsanaChangesService
import com.ft.monitoring.ProjectChange
import org.junit.Rule
import org.springframework.boot.test.OutputCapture
import spock.lang.Specification

class BotSchedulerSpec extends Specification {
    private BotScheduler botScheduler
    private AsanaService mockAsanaService = Mock(AsanaService)
    private AsanaBackupService mockAsanaBackupService = Mock(AsanaBackupService)
    private AsanaChangesService mockAsanaChangesService = Mock(AsanaChangesService)
    private SlackService mockSlackService = Mock(SlackService)

    @Rule
    OutputCapture capture = new OutputCapture()

    void setup() {
        botScheduler = new BotScheduler()
        botScheduler.asanaService = mockAsanaService
        botScheduler.asanaBackupService = mockAsanaBackupService
        botScheduler.asanaChangesService = mockAsanaChangesService
        botScheduler.slackService = mockSlackService

        capture.flush()
    }

    void "GraphicsBot"() {
        when:
            botScheduler.graphicsBot()

        then:
            1 * mockAsanaService.addGraphicsProjectToGraphicsBotAssignedTasks()
            0 * _
    }

    void "PicturesBot"() {
        when:
            botScheduler.picturesBot()

        then:
            1 * mockAsanaService.addPicturesProjectToPicturesBotAssignedTasks()
            0 * _
    }

    void "backupAllProjects"() {
        when:
            botScheduler.backupAllProjects()

        then:
            1 * mockAsanaBackupService.backupAllProjects()
            0 * _
    }

    void "backupAllProjects should log error when IOException was caught"() {
        when:
            botScheduler.backupAllProjects()

        then:
            1 * mockAsanaBackupService.backupAllProjects() >> { throw new IOException("test message")}
            0 * _
        and:
            capture.toString().contains("java.io.IOException: test message")
    }

    void "removeOldBackupFiles"() {
        when:
            botScheduler.removeOldBackupFiles()

        then:
            1 * mockAsanaBackupService.removeOldBackupFiles()
            0 * _
    }

    void "removeOldBackupFiles should log error when IOException was caught"() {
        when:
            botScheduler.removeOldBackupFiles()

        then:
            1 * mockAsanaBackupService.removeOldBackupFiles() >> { throw new IOException("test message")}
            0 * _
        and:
            capture.toString().contains("java.io.IOException: test message")
    }

    void "checkForChanges passes changes to slack notifier"() {
        given:
            ProjectChange change1 = new ProjectChange(null)
            ProjectChange change2 = new ProjectChange(null)
            List<ProjectChange> changes = [change1, change2]
        when:
            botScheduler.checkForChanges()
        then:
            1 * mockAsanaChangesService.getChanges() >> changes
            1 * mockSlackService.notifyProjectChange(changes)
            0 * _

    }
}
