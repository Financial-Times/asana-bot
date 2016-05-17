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
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import java.util.ArrayList;
import java.util.List;

public class AsanaClient {
    private static final String PROJECT_TASK_OPT_EXPAND = "(this|subtasks+)";
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


    //Can't deprecate this one as there is no /users/{user-id}/teams endpoint in asana-client
    public List<Team> findTeams(String email) {
        User user =  findUserByEmail(email);
        TeamsData teamsData = asana.getUserTeams(config.getWorkspace(), user.getId());
        return teamsData.getData();
    }

    @Deprecated //See AsanaClientWrapper
    public List<ReportTask> findTaskItems(Long projectId, String completedSince) {
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

    @Deprecated //See AsanaClientWrapper
    public List<ProjectInfo> getAllProjects() {
        ProjectsData projectsData = asana.getMyProjects(config.getWorkspace(), "this");
        return projectsData.getData();
    }

    @Deprecated //See AsanaClientWrapper
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
