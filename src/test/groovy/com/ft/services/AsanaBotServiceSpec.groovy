package com.ft.services

import com.asana.models.Project
import com.asana.models.Tag
import com.asana.models.Task
import com.asana.models.Team
import com.ft.asanaapi.AsanaClientWrapper
import com.ft.config.Config
import com.ft.config.TaskBot
import spock.lang.Specification

class AsanaBotServiceSpec extends Specification {
    private AsanaBotService service
    private AsanaClientWrapper mockClient
    private Config config

    void setup() {
        mockClient = Mock(AsanaClientWrapper)
        TaskBot bot = new TaskBot('test name', '11223344', 'dummmy key', mockClient)
        List<TaskBot> bots = [bot]
        config = new Config(bots: bots, workspace: '223344', tags: [:])
        service = new AsanaBotService(config)
    }

    void runBots() {
        given:
            Project project = new Project()
            Task task = new Task()
            task.projects = [project]
            List<Task> tasks = [task]
        when:
            service.runAllBots()
        then:
            1 * mockClient.getTasks(config.workspace) >> tasks
            1 * mockClient.getProject('11223344') >> project
            1 * mockClient.addTaskToProject(task, project)
            1 * mockClient.unassignTask(task)
        and:
            0 * _
    }

    void 'runBots - subtask'() {
        given:
            Team team = new Team(name: 'test team')
            Project project = new Project(name: 'test project', team: team)
            Task task = new Task(projects: [project])
            Task subtask = new Task(projects: [], parent: task, name: 'test sub task')
            List<Task> tasks = [subtask]
            Tag tag = new Tag()
            String comment = "I have added the task ${subtask.name} to ${project.name}"
        when:
            service.runAllBots()
        then:
            1 * mockClient.getTasks(config.workspace) >> tasks
            1 * mockClient.getProject('11223344') >> project
            1 * mockClient.addTaskToProject(subtask, project)
            1 * mockClient.findTagsByWorkspace('223344') >> []
            1 * mockClient.createTag('223344', 'test team') >> tag
            1 * mockClient.tagTask(subtask, tag)
            1 * mockClient.commentTask(task, comment)
            1 * mockClient.unassignTask(subtask)
        and:
            0 * _
    }

    void "runBots - IOException doesn't break task loop"() {
        given:
            Project project = new Project()
            Task task1 = new Task()
            task1.projects = [project]
            Task task2 = new Task()
            task2.projects = [project]
            List<Task> tasks = [task1, task2]
        when:
            service.runAllBots()
        then:
            1 * mockClient.getTasks(config.workspace) >> tasks
            1 * mockClient.getProject('11223344') >> project
            1 * mockClient.addTaskToProject(task1, project) >> {throw new IOException()}
            1 * mockClient.addTaskToProject(task2, project)
            1 * mockClient.unassignTask(task2)
        and:
            0 * _
    }


}
