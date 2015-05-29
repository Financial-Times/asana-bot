package com.ft.asanaapi;

import com.ft.asanaapi.auth.BasicAuthRequestInterceptor;
import com.ft.asanaapi.model.*;
import com.ft.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;

import java.util.*;

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
        TasksData tasksData = asana.tasks("me", config.getWorkspace(), "now", "id,name,parent.id,parent.name,parent.projects.team.name,projects.team.name");
        logTaskProcessingStart(projectId, tasksData);
        List<Task> tasks = tasksData.getData();

        ProjectInfo projectInfo = asana.project(projectId).getData();

        tasks.stream().forEach(task -> {
            asana.addProjectToTask(task.getId(), projectId);

            if (task.isSubTask()) {
                addCommentToParent(projectInfo, task);
            }

            ProjectInfo originalProject = extractProjectFromTask(task);
            if (originalProject.isAssignedToTeam()) {
                Tag tag = findOrCreateTagByName(originalProject.getTeam());
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

    private ProjectInfo extractProjectFromTask(Task task) {
        if (task.isSubTask()) {
            return task.getParent().getProjects().get(0);
        }
        return task.getProjects().get(0);
    }

    private Tag findOrCreateTagByName(Team team) {
        String tagName = mapTeamToTag(team);
        List<Tag> existingTags = asana.queryForTag(config.getWorkspace(), tagName).getData();
        Optional<Tag> existingTag = existingTags.stream()
                .filter(tag -> tag.getName().equals(team.getName()))
                .findFirst();

        if (existingTag.isPresent()) {
            return existingTag.get();
        }

        return asana.createTag(config.getWorkspace(), team.getName()).getData();
    }

    private String mapTeamToTag(Team team) {
        Map<String, String> tagToTeamMapping = new HashMap<>();
        tagToTeamMapping.put("Companies", "COS");
        tagToTeamMapping.put("World", "WN");
        tagToTeamMapping.put("UK", "UKN");
        tagToTeamMapping.put("Pictures", "PIC");
        return tagToTeamMapping.getOrDefault(team.getName(), team.getName());
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
