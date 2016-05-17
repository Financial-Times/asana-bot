package com.ft.asanaapi;

import com.ft.asanaapi.model.ProjectData;
import com.ft.asanaapi.model.TasksData;
import com.ft.asanaapi.model.TeamsData;
import com.ft.asanaapi.model.UserData;
import com.ft.backup.model.BackupTasksData;
import com.ft.backup.model.ProjectsData;
import com.ft.report.model.ReportTasksData;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Asana {

    @GET("/tasks")
    TasksData tasks(
            @Query("assignee") String assignee,
            @Query("workspace") String workspace,
            @Query("completed_since") String completedSince,
            @Query("opt_fields") String optionalFields
    );

    @GET("/tasks")
    ReportTasksData openProjectTasks(
            @Query("workspace") String workspace,
            @Query("project") Long projectId,
            @Query("completed_since") String completedSince,
            @Query("opt_fields") String optionalFields
    );

    @GET("/projects/{project-id}")
    ProjectData project(
            @Path("project-id") String projectId
    );

    @GET("/users/{user-id}/teams")
    TeamsData getUserTeams(
            @Query("organization") String organizationId,
            @Path("user-id") String userId
    );

    @GET("/workspaces/{workspace-id}/typeahead?type=user")
    UserData getUserByEmail(
            @Path("workspace-id") String workspaceId,
            @Query("query") String email);

    @GET("/projects")
    ProjectsData getMyProjects(
            @Query("workspace") String workspace,
            @Query("opt_expand") String optionalFields);

    @GET("/projects/{project-id}/tasks")
    BackupTasksData getAllTasksByProject(@Path("project-id") String projectId,
                                 @Query("opt_expand") String optionalFields,
                                 @Query("limit") String limit,
                                 @Query("offset") String nextPage);
}
