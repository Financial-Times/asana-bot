package com.ft.asanaapi;

import com.ft.backup.model.BackupTasksData;
import com.ft.backup.model.ProjectsData;
import com.ft.asanaapi.model.*;
import com.ft.report.model.ReportTasksData;
import retrofit.client.Response;
import retrofit.http.*;

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
            @Query("project") String project,
            @Query("completed_since") String completedSince,
            @Query("opt_fields") String optionalFields
    );

    @GET("/projects/{project-id}")
    ProjectData project(
            @Path("project-id") String projectId
    );

    @FormUrlEncoded
    @POST("/tasks/{task-id}/addProject")
    Response addProjectToTask(
            @Path("task-id") String taskId,
            @Field("project") String projectId
    );

    @GET("/tasks/{task-id}")
    TaskData getTask(
            @Path("task-id") String taskId,
            @Query("opt_fields") String optionalFields
    );

    @FormUrlEncoded
    @PUT("/tasks/{task-id}")
    Response updateTask(
            @Path("task-id") String taskId,
            @Field("assignee") String assignee
    );

    @FormUrlEncoded
    @POST("/tasks/{task-id}/stories")
    Response commentOnTask(
            @Path("task-id") String taskId,
            @Field("text") String text
    );

    @GET("/workspaces/{workspace-id}/typeahead?type=tag")
    TagsData queryForTag(
            @Path("workspace-id") String workspaceId,
            @Query("query") String tagName
    );

    @FormUrlEncoded
    @POST("/tasks/{task-id}/addTag")
    Response addTagToTask(
            @Path("task-id") String taskId,
            @Field("tag") String tagId
    );

    @FormUrlEncoded
    @POST("/tasks/{task-id}/removeTag")
    Response removeTagFromTask(
            @Path("task-id") String taskId,
            @Field("tag") String tagId
    );

    @FormUrlEncoded
    @POST("/workspaces/{workspace-id}/tags")
    TagData createTag(
            @Path("workspace-id") String workspaceId,
            @Field("name")String tagName
    );

    @GET("/workspaces/{workspace-id}/tags")
    TagsData getAllWorkspaceTags(@Path("workspace-id") String workspaceId);

    @GET("/tags/{tag-id}/tasks?opt_expand=this")
    TasksData getTasksByTag(@Path("tag-id") String tagId);

    @GET("/users/{user-id}/teams")
    TeamsData getUserTeams(
            @Query("organization") String organizationId,
            @Path("user-id") String userId
    );

    @GET("/workspaces/{workspace-id}/typeahead?type=user")
    UserData getUserByEmail(
            @Path("workspace-id") String workspaceId,
            @Query("query") String email);

    @GET("/workspaces/{workspace-id}")
    Response ping(@Path("workspace-id") String workspaceId);

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
