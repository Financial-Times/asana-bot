package com.ft.tasks;

import com.asana.models.Project;
import com.asana.models.Tag;
import com.asana.models.Task;
import com.asana.models.Team;
import com.ft.asanaapi.AsanaClientWrapper;
import com.ft.config.TaskBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Primary
@Component("taskOnProjectTaskRunner")
public class TaskOnProjectTaskRunner implements TaskRunner {

    private static final Logger logger = LoggerFactory.getLogger(TaskOnProjectTaskRunner.class);
    public static final int MAX_ATTEMPTS = 5;

    @Override
    public void run(final TaskBot taskBot) {

        AsanaClientWrapper client = taskBot.getClient();
        final String botName = taskBot.getName();
        try {
            List<Task> tasks = client.getTasks();
            Project newProject = client.getProject(taskBot.getProjectId());

            for (Task task: tasks) {
                try {
                    Project originalProject = extractProjectFromTask(task,client, 0);
                    client.addTaskToProject(task, newProject);
                    if (originalProject != null && originalProject.team != null) {
                        Tag tag = findOrCreateTagByName(originalProject.team, taskBot);
                        client.tagTask(task, tag);
                    }
                    if (isSubTask(task)) {
                        addCommentToParent(newProject, task, client);
                    }
                    client.unassignTask(task);
                    logTaskProcessingSuccess(botName,newProject, task);
                } catch (IOException e) {
                    logger.error("Could not process task: " + task.id, e);
                }
            }
        } catch (IOException e) {
            logger.error("Could not fetch tasks", e);
        }
    }

    private boolean isSubTask(Task task) {
        return task.parent != null;
    }

    private Project extractProjectFromTask(Task task, AsanaClientWrapper client, Integer attempts) throws IOException {
        Optional<Project> candidate = task.projects.stream().findFirst();
        if (candidate.isPresent()) {
            return candidate.get();
        }
        Task parent = task.parent;
        if (parent == null) {
            parent = client.getTask(task.id);
        }
        if (attempts >= MAX_ATTEMPTS) {
            return null;
        }
        return extractProjectFromTask(parent, client, ++attempts);
    }

    private Tag findOrCreateTagByName(Team team, TaskBot taskBot) throws IOException {
        String tagName = mapTeamToTag(team, taskBot);
        AsanaClientWrapper client = taskBot.getClient();
        Optional<Tag> tagOptional = client.findTagsByWorkspace(tagName);
        if (tagOptional.isPresent()) {
            Tag existingTag = tagOptional.get();
            if (tagName.equals(existingTag.name)) {
                return existingTag;
            }
        }
        return client.createTag(tagName);
    }

    private String mapTeamToTag(Team team, TaskBot taskBot) {
        return taskBot.getTags().getOrDefault(team.name, team.name);
    }

    private void addCommentToParent(Project project, Task task, AsanaClientWrapper client) throws IOException {
        String comment = "I have added the task " + task.name + " to " + project.name;
        client.commentTask(task.parent, comment);
    }

    private void logTaskProcessingSuccess(final String botName, final Project project, final Task task) {
        logger.info("{} bot successfully added task {} to {} project.", botName, task.id, project.id);
    }
}
