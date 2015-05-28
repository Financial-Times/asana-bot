package com.ft.asanaapi;

import com.ft.asanaapi.auth.BasicAuthRequestInterceptor;
import com.ft.asanaapi.model.*;
import com.ft.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;

import java.util.List;
import java.util.Optional;

public class AsanaClient {

    private static Logger logger = LoggerFactory.getLogger(AsanaClient.class);

    private Config config;
    private Asana asana;

    public AsanaClient(String apiKey, Config config) {

        this.config = config;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor((new BasicAuthRequestInterceptor()).setPassword(apiKey))
                .setEndpoint(config.getBaseUrl())
                .build();

        asana = restAdapter.create(Asana.class);
    }

    public void addProjectToCurrentlyAssignedIncompleteTasks(String projectId) {

        //get list of assigned tasks
        TasksData tasksData = asana.tasks("me", config.getWorkspace(), "now", "id,name,parent.id,parent.name");

        logTaskProcessingStart(projectId, tasksData);

        ProjectInfo projectInfo = asana.project(projectId).getData();
        List<Tag> tags = asana.tags(config.getWorkspace()).getData();

        List<Task> tasks = tasksData.getData();

        tasks.stream().forEach(task -> {
            asana.addProjectToTask(task.getId(), projectId);

            if (task.isSubTask()) {
                addCommentToParent(projectInfo, task);
            }

            if (projectInfo.isAssignedToTeam()) {
                Tag tag = findOrCreateTagByName(projectInfo.getTeam(), tags);
                asana.addTagToTask(task.getId(), tag.getId());
            }

            unassignTask(task);
            logTaskProcessingSuccess(projectId, task);
        });
    }

    private void logTaskProcessingStart(String projectId, TasksData tasksData) {
        if (logger.isDebugEnabled()) {
            logger.debug("About to add tasks to project {}", projectId);
            logger.debug(tasksData.toString());
        }
    }

    private void addCommentToParent(ProjectInfo projectInfo, Task task) {
        asana.commentOnTask(task.getParent().getId(),
                "I have added the task " + task.getName() + " to " + projectInfo.getName());
    }

    private Tag findOrCreateTagByName(Team team, List<Tag> tags) {
        Optional<Tag> existingTag = tags.stream()
                .filter(tag -> tag.getName().equals(team.getName()))
                .findFirst();

        if (existingTag.isPresent()) {
            return existingTag.get();
        }
        Tag newTag = asana.createTag(config.getWorkspace(), team.getName()).getData();
        tags.add(newTag);
        return newTag;
    }

    private void unassignTask(Task task) {
        asana.updateTask(task.getId(), "null");
    }

    private void logTaskProcessingSuccess(String projectId, Task task) {
        if (logger.isDebugEnabled()) {
            logger.debug("Successfully added task {} to {} project.", task, projectId);
        }
    }
}
