package com.ft.services
import com.asana.models.Project
import com.asana.models.Tag
import com.asana.models.Task
import com.asana.models.Team
import com.ft.asanaapi.AsanaClientWrapper
import com.ft.config.Config
import com.ft.config.TaskBot
import com.ft.tasks.TaskDueDateTaskRunner
import com.ft.tasks.TaskOnProjectTaskRunner
import com.ft.tasks.TaskRunnerFactory
import spock.lang.Specification

class AsanaBotServiceSpec extends Specification {
    private static final int TWENTY_SECONDS = 20_000
    private AsanaBotService service
    private AsanaClientWrapper mockClient
    private Config config
    private TaskOnProjectTaskRunner mockTaskRunner;
    private TaskDueDateTaskRunner mockDueDateTaskRunner;
    private TaskBot bot;
    private TaskRunnerFactory mockTaskRunnerFactory;

    void setup() {
        mockClient = Mock(AsanaClientWrapper)
        mockTaskRunner = Spy(TaskOnProjectTaskRunner)
        mockDueDateTaskRunner = Spy(TaskDueDateTaskRunner)
        mockTaskRunnerFactory = Mock(TaskRunnerFactory);
        bot = new TaskBot('test name', '11223344', 'dummmy key', mockClient, 'runner', '223344', [:], TWENTY_SECONDS)
        List<TaskBot> bots = [bot]
        config = new Config(bots: bots, workspace: '223344', tags: [:])
        service = new AsanaBotService(config, mockTaskRunnerFactory)
    }

    void runBots() {
        given:
            Project project = new Project()
            Task task = new Task()
            task.projects = [project]
            List<Task> tasks = [task]
        when:
            service.runBots(TWENTY_SECONDS)
        then:
            1 * mockTaskRunnerFactory.getTaskRunner('runner') >> mockTaskRunner
            1 * mockTaskRunner.run(bot)
        and:
            1 * mockClient.getTasks('223344') >> tasks
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
            service.runBots(TWENTY_SECONDS)
        then:
            1 * mockTaskRunnerFactory.getTaskRunner('runner') >> mockTaskRunner
            1 * mockTaskRunner.run(bot)
        and:
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
            service.runBots(TWENTY_SECONDS)
        then:
            1 * mockTaskRunnerFactory.getTaskRunner('runner') >> mockTaskRunner
            1 * mockTaskRunner.run(bot)
        and:
            1 * mockClient.getTasks(config.workspace) >> tasks
            1 * mockClient.getProject('11223344') >> project
            1 * mockClient.addTaskToProject(task1, project) >> {throw new IOException()}
            1 * mockClient.addTaskToProject(task2, project)
            1 * mockClient.unassignTask(task2)
        and:
            0 * _

    }

    void 'runBots - due date setter'() {
        given:
            mockDueDateTaskRunner = Spy(TaskDueDateTaskRunner)
            Task task = new Task()
            task.name = "names|2016-01-01T09:10:46.449Z"
            List<Task> tasks = [task]
        when:
            service.runBots(TWENTY_SECONDS)
        then:
            1 * mockTaskRunnerFactory.getTaskRunner('runner') >> mockDueDateTaskRunner
            1 * mockDueDateTaskRunner.run(bot)
        and:
            1 * mockClient.getTasksByProject('11223344') >> tasks
            1 * mockClient.updateTask(task, _)
        and:
            0 * _

    }


}
