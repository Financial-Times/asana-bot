package com.ft.asanaapi;

import com.ft.asanaapi.auth.BasicAuthRequestInterceptor;
import com.ft.asanaapi.auth.NonAsanaUserException;
import com.ft.asanaapi.model.*;
import com.ft.backup.model.BackupTask;
import com.ft.backup.model.BackupTasksData;
import com.ft.backup.model.ProjectsData;
import com.ft.config.Config;
import com.ft.report.model.ReportTask;
import com.ft.report.model.ReportTasksData;
import com.squareup.okhttp.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;

import java.util.*;

public class AsanaClient {

    private static final Logger logger = LoggerFactory.getLogger(AsanaClient.class);
    private static final String PROJECT_TASK_OPT_EXPAND = "(this|subtasks+)";
    public static final String TASK_FIELDS = "id,name,parent.id,parent.name,parent.projects.team.name,projects.team.name";
    public static final String ASANA_TASKS_LIMIT = "100";

    private Config config;
    private Asana asana;

    public AsanaClient(String apiKey, Config config, OkHttpClient okHttpClient) {
        this.config = config;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor((new BasicAuthRequestInterceptor()).setPassword(apiKey))
                .setEndpoint(config.getBaseUrl())
                .setClient(new OkClient(okHttpClient))
                .build();
        asana = restAdapter.create(Asana.class);
    }

    public void addProjectToCurrentlyAssignedIncompleteTasks(String projectId) {

        //get list of assigned tasks
        TasksData tasksData = asana.tasks("me", config.getWorkspace(), "now", TASK_FIELDS);
        logTaskProcessingStart(projectId, tasksData);
        List<Task> tasks = tasksData.getData();

        ProjectInfo projectInfo = asana.project(projectId).getData();

        tasks.stream().forEach(task -> {
            asana.addProjectToTask(task.getId(), projectId);

            ProjectInfo originalProject = extractProjectFromTask(task);
            if (originalProject.isAssignedToTeam()) {
                Tag tag = findOrCreateTagByName(originalProject.getTeam());
                asana.addTagToTask(task.getId(), tag.getId());
            }
            if (task.isSubTask()) {
                addCommentToParent(projectInfo, task);
            }

            unassignTask(task);
            logTaskProcessingSuccess(projectId, task);
        });
    }

    public List<Team> findTeams(String email) {
        User user =  findUserByEmail(email);
        TeamsData teamsData = asana.getUserTeams(config.getWorkspace(), user.getId());
        return teamsData.getData();
    }

    public List<ReportTask> findTaskItems(String projectId, String completedSince) {
        String optionalFields = "name,tags.name,due_on,notes,completed,subtasks.name,subtasks.completed";
        ReportTasksData reportTasksData = asana.openProjectTasks(config.getWorkspace(), projectId, completedSince, optionalFields);
        return reportTasksData.getData();
    }

    private User findUserByEmail(String email) {
        UserData userData = asana.getUserByEmail(config.getWorkspace(), email);
        if (userData == null || userData.getData() == null || userData.getData().size() == 0) {
            throw new NonAsanaUserException();
        }
        return userData.getData().get(0);
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
            return extractProjectFromParentTask(task.getParent());
        }
        return extractProjectFromParentTask(task);
    }

    private ProjectInfo extractProjectFromParentTask(Task task) {
        List<ProjectInfo> candidate = task.getProjects();
        if (candidate != null && !candidate.isEmpty()) {
            return candidate.get(0);
        }
        Task detailedTask = retrieveTask(task);
        return extractProjectFromParentTask(detailedTask.getParent());
    }

    private Task retrieveTask(Task task) {
        return asana.getTask(task.getId(), TASK_FIELDS).getData();
    }

    private Tag findOrCreateTagByName(Team team) {
        String tagName = mapTeamToTag(team);
        List<Tag> existingTags = asana.queryForTag(config.getWorkspace(), tagName).getData();
        Optional<Tag> existingTag = existingTags.stream()
                .filter(tag -> tag.getName().equals(tagName))
                .findFirst();

        if (existingTag.isPresent()) {
            return existingTag.get();
        }

        return asana.createTag(config.getWorkspace(), tagName).getData();
    }

    private String mapTeamToTag(Team team) {
        return config.getTags().getOrDefault(team.getName(), team.getName());
    }

    private void unassignTask(Task task) {
        asana.updateTask(task.getId(), "null");
    }

    private void logTaskProcessingSuccess(String projectId, Task task) {
        if (logger.isDebugEnabled()) {
            logger.debug("Successfully added task {} to {} project.", task, projectId);
        }
    }

    public Response ping() {
        return asana.ping(config.getWorkspace());
    }

    public List<ProjectInfo> getAllProjects() {
        ProjectsData projectsData = asana.getMyProjects(config.getWorkspace(), "this");
        return projectsData.getData();
    }

    public List<BackupTask> getAllTasksByProject(ProjectInfo project) {
        List<BackupTask> tasks = new ArrayList<>();
        BackupTasksData data = asana.getAllTasksByProject(project.getId(), PROJECT_TASK_OPT_EXPAND, ASANA_TASKS_LIMIT, null);
        tasks.addAll(data.getData());
        while (data.getNextPage() != null) {
            data = asana.getAllTasksByProject(project.getId(), PROJECT_TASK_OPT_EXPAND, "100", data.getNextPage().getOffset());
            tasks.addAll(data.getData());
        }
        return tasks;
    }
}
