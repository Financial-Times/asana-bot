package com.ft.services

import com.ft.backup.AsanaBackupService
import org.junit.Rule
import org.springframework.boot.test.OutputCapture
import spock.lang.Specification

class BotSchedulerSpec extends Specification {
    private BotScheduler botScheduler
    private AsanaService mockAsanaService = Mock(AsanaService)
    private AsanaBackupService mockAsanaBackupService = Mock(AsanaBackupService)

    @Rule
    OutputCapture capture = new OutputCapture()

    void setup() {
        botScheduler = new BotScheduler()
        botScheduler.asanaService = mockAsanaService
        botScheduler.asanaBackupService = mockAsanaBackupService

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

    void "SocialBot"() {
        when:
            botScheduler.socialBot()

        then:
            1 * mockAsanaService.addSocialProjectToSocialBotAssignedTasks()
            0 * _
    }

    void "InteractivesBot"() {
        when:
            botScheduler.interactivesBot()

        then:
            1 * mockAsanaService.addInteractivesProjectToInteractivesBotAssignedTasks()
            0 * _
    }

    void "VideoBot"() {
        when:
            botScheduler.videoBot()

        then:
            1 * mockAsanaService.addVideoProjectToVideoBotAssignedTasks()
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
}
