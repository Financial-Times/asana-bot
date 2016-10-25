package com.ft.tasks

import com.asana.models.Project as AsanaProject
import com.asana.models.Team
import com.ft.asanaapi.AsanaClientWrapper
import com.ft.config.TaskBot
import com.ft.monitoring.DeskConfig
import com.ft.monitoring.ProjectChange
import com.ft.report.model.Desk
import com.ft.report.model.Project
import com.ft.services.SlackService
import spock.lang.Specification

class CheckForChangesTaskRunnerSpec extends Specification {
    private CheckForChangesTaskRunner changesTaskRunner
    private SlackService mockSlackService
    private DeskConfig deskConfig

    void setup() {
        mockSlackService = Mock(SlackService)
        deskConfig = new DeskConfig()
        changesTaskRunner = new CheckForChangesTaskRunner(mockSlackService, deskConfig)
    }
    void "run with no changes"() {
        given:
            TaskBot mockTaskBot = Mock(TaskBot)
            AsanaClientWrapper mockClient = Mock(AsanaClientWrapper)
        and:
            Project project = new Project(id: 12345, name: 'test project')
            Desk desk = new Desk(projects: [project])
            deskConfig.desks = ['test desk': desk]
        and:
            Team team = new Team(name: 'test desk')
            AsanaProject asanaProject = new AsanaProject(id: 12345, name: 'test project', team: team)
        when:
            changesTaskRunner.run(mockTaskBot)
        then:
            1 * mockTaskBot.client >> mockClient
            1 * mockClient.getAllProjects() >> [asanaProject]
            0 * _
    }

    void "run when project name change"() {
        given:
            TaskBot mockTaskBot = Mock(TaskBot)
            AsanaClientWrapper mockClient = Mock(AsanaClientWrapper)
        and:
            Project project = new Project(id: 12345, name: 'test project')
            Desk desk = new Desk(projects: [project])
            deskConfig.desks = ['test desk': desk]
        and:
            Team team = new Team(name: 'test desk')
            AsanaProject newProject = new AsanaProject(id: '12345', name: 'Project 1 renamed', team: team)
            AsanaProject referenceProject = new AsanaProject(id: '12345', name: 'test project', team: team)
            ProjectChange projectChange = new ProjectChange(newProject, referenceProject)
            List<ProjectChange> changes = [projectChange]
        when:
            changesTaskRunner.run(mockTaskBot)
        then:
            1 * mockTaskBot.client >> mockClient
            1 * mockClient.getAllProjects() >> [newProject]
            1 * mockSlackService.notifyProjectChange(changes)
            0 * _
    }

    void "run when project was archived"() {
        given:
            TaskBot mockTaskBot = Mock(TaskBot)
            AsanaClientWrapper mockClient = Mock(AsanaClientWrapper)
        and:
            Project project = new Project(id: 12345, name: 'test project')
            Desk desk = new Desk(projects: [project])
            deskConfig.desks = ['test desk': desk]
        and:
            Team team = new Team(name: 'test desk')
            AsanaProject newProject = new AsanaProject(id: '12345', name: 'Project 1 renamed', team: team, isArchived: true)
            AsanaProject referenceProject = new AsanaProject(id: '12345', name: 'test project', team: team, isArchived: false)
            ProjectChange projectChange = new ProjectChange(newProject, referenceProject)
            List<ProjectChange> changes = [projectChange]
        when:
            changesTaskRunner.run(mockTaskBot)
        then:
            1 * mockTaskBot.client >> mockClient
            1 * mockClient.getAllProjects() >> [newProject]
            1 * mockSlackService.notifyProjectChange(changes)
            0 * _
    }

    void "run when project team was changed"() {
        given:
            TaskBot mockTaskBot = Mock(TaskBot)
            AsanaClientWrapper mockClient = Mock(AsanaClientWrapper)
        and:
            Project project = new Project(id: 12345, name: 'test project')
            Desk desk = new Desk(projects: [project])
            deskConfig.desks = ['test desk': desk]
        and:
            Team oldTeam = new Team(name: 'test desk')
            Team newTeam = new Team(name: 'another desk')
            AsanaProject newProject = new AsanaProject(id: '12345', name: 'Project 1 renamed', team: newTeam)
            AsanaProject referenceProject = new AsanaProject(id: '12345', name: 'test project', team: oldTeam)
            ProjectChange projectChange = new ProjectChange(newProject, referenceProject)
            List<ProjectChange> changes = [projectChange]
        when:
            changesTaskRunner.run(mockTaskBot)
        then:
            1 * mockTaskBot.client >> mockClient
            1 * mockClient.getAllProjects() >> [newProject]
            1 * mockSlackService.notifyProjectChange(changes)
            0 * _
    }

    void "run when project not found"() {
        given:
            TaskBot mockTaskBot = Mock(TaskBot)
            AsanaClientWrapper mockClient = Mock(AsanaClientWrapper)
        and:
            Project project = new Project(id: 12345, name: 'test project')
            Desk desk = new Desk(projects: [project])
            deskConfig.desks = ['test desk': desk]
        and:
            Team team = new Team(name: 'test desk')
            AsanaProject referenceProject = new AsanaProject(id: '12345', name: 'test project', team: team)
            ProjectChange projectChange = new ProjectChange(null, referenceProject)
            List<ProjectChange> changes = [projectChange]
        when:
            changesTaskRunner.run(mockTaskBot)
        then:
            1 * mockTaskBot.client >> mockClient
            1 * mockClient.getAllProjects() >> []
            1 * mockSlackService.notifyProjectChange(changes)
            0 * _
    }

    void "run find project in comma separated list"() {
        given:
            TaskBot mockTaskBot = Mock(TaskBot)
            AsanaClientWrapper mockClient = Mock(AsanaClientWrapper)
        and:
            Project project = new Project(id: '12345,456789', name: 'test project,test project 2')
            Desk desk = new Desk(projects: [project])
            deskConfig.desks = ['test desk': desk]
        and:
            Team team = new Team(name: 'test desk')
            AsanaProject asanaProject = new AsanaProject(id: 12345, name: 'test project', team: team)
            AsanaProject asanaProject2 = new AsanaProject(id: 456789, name: 'test project 2', team: team)
        when:
            changesTaskRunner.run(mockTaskBot)
        then:
            1 * mockTaskBot.client >> mockClient
            1 * mockClient.getAllProjects() >> [asanaProject, asanaProject2]
            0 * _
    }
}
