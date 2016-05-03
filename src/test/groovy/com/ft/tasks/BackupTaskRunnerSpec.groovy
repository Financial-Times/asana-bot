package com.ft.tasks

import com.ft.backup.AsanaBackupService
import com.ft.config.TaskBot
import spock.lang.Specification

class BackupTaskRunnerSpec extends Specification {
    private BackupTaskRunner backupTaskRunner
    private AsanaBackupService mockAsanaBackupService

    void setup() {
        mockAsanaBackupService = Mock(AsanaBackupService)
        backupTaskRunner = new BackupTaskRunner(mockAsanaBackupService)
    }
    void run() {
        given:
            TaskBot taskBot = Mock(TaskBot)
        when:
            backupTaskRunner.run(taskBot)
        then:
            1 * mockAsanaBackupService.backupAllProjects()
            0 * _
    }
}
