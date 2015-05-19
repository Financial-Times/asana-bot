package com.ft.asanaapi;

import com.ft.asanaapi.model.EmptyData;
import com.ft.asanaapi.model.ProjectData;
import com.ft.asanaapi.model.TasksData;
import retrofit.http.*;

public interface Asana {

    /**
     * Get all tasks in project.
     *
     * @param assignee
     * @param workspace
     * @param completedSince
     * @param optionalFields
     * @return
     */
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
    EmptyData addProjectToTask(
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
    EmptyData commentOnTask(
            @Path("task-id") String taskId,
            @Field("text") String text
    );
}
