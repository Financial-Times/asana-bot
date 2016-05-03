package com.ft.tasks

import com.ft.backup.AsanaBackupService
import com.ft.config.TaskBot
import spock.lang.Specification

class RemoveOldBackupFilesTaskRunnerSpec extends Specification {
    private RemoveOldBackupFilesTaskRunner removeOldBackupFilesTaskRunner
    private AsanaBackupService mockAsanaBackupService

    void setup() {
        mockAsanaBackupService = Mock(AsanaBackupService)
        removeOldBackupFilesTaskRunner = new RemoveOldBackupFilesTaskRunner(mockAsanaBackupService)
    }
    void run() {
        given:
            TaskBot taskBot = Mock(TaskBot)
        when:
            removeOldBackupFilesTaskRunner.run(taskBot)
        then:
            1 * mockAsanaBackupService.removeOldBackupFiles()
            0 * _
    }
}
