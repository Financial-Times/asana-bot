package com.ft.tasks

import com.ft.config.TaskBot
import com.ft.monitoring.AsanaChangesService
import com.ft.monitoring.ProjectChange
import com.ft.services.SlackService
import spock.lang.Specification

class CheckForChangesTaskRunnerSpec extends Specification {
    private CheckForChangesTaskRunner changesTaskRunner
    private AsanaChangesService mockAsanaChangeService
    private SlackService mockSlackService

    void setup() {
        mockAsanaChangeService = Mock(AsanaChangesService)
        mockSlackService = Mock(SlackService)
        changesTaskRunner = new CheckForChangesTaskRunner(mockAsanaChangeService, mockSlackService)
    }
    void "run with no changes"() {
        given:
            TaskBot mockTaskBot = Mock(TaskBot)
        when:
            changesTaskRunner.run(mockTaskBot)
        then:
            1 * mockAsanaChangeService.getChanges()
            0 * _
    }

    void "run with changes"() {
        given:
            TaskBot mockTaskBot = Mock(TaskBot)
            ProjectChange change1 = Mock(ProjectChange)
            ProjectChange change2 = Mock(ProjectChange)
            List<ProjectChange> changes = [change1, change2]
        when:
            changesTaskRunner.run(mockTaskBot)
        then:
            1 * mockAsanaChangeService.getChanges() >> changes
            1 * mockSlackService.notifyProjectChange(changes)
            0 * _
    }
}
