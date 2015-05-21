package com.ft.asanaapi;

import com.ft.asanaapi.model.ProjectData;
import com.ft.asanaapi.model.TasksData;
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

    @FormUrlEncoded
    @PUT("/tasks/{task-id}")
    TasksData updateTask(
            @Path("task-id") String taskId,
            @Field("assignee") String assignee
    );

    @FormUrlEncoded
    @POST("/tasks/{task-id}/stories")
    Response commentOnTask(
            @Path("task-id") String taskId,
            @Field("text") String text
    );
}
