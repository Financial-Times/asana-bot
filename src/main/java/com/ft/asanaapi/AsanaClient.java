package com.ft.asanaapi;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.*;

import java.util.Base64;
import java.util.List;

public class AsanaClient {

    public static final String ASANA_API_URL = "https://app.asana.com/api/1.0";

    private String workspaceId;
    private Asana asana;

    class BasicAuthRequestInterceptor implements RequestInterceptor {

        private String password;

        @Override
        public void intercept(RequestFacade requestFacade) {

            if (password != null) {
                final String authorizationValue = encodeCredentialsForBasicAuthorization();
                requestFacade.addHeader("Authorization", authorizationValue);
            }
        }

        private String encodeCredentialsForBasicAuthorization() {
            final String userAndPassword =  password + ":";
            return "Basic " + Base64.getEncoder().encodeToString(userAndPassword.getBytes());
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class TaskData {
        static class Task {
            String id;
            String name;

            @Override
            public String toString() {
                return "id: " + id + " , name: " + name;
            }
        }
        List<Task> data;
    }

    static class ProjectData {
        static class ProjectInfo {
            String id;
            String name;
            String notes;
        }
        ProjectInfo data;
    }

    static class EmptyData {
    }

    interface Asana {
        @GET("/tasks")
        TaskData tasks(
                @Query("assignee") String assignee,
                @Query("workspace") String workspace,
                @Query("completed_since") String completedSince
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
        TaskData updateTask(
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

    public AsanaClient(String apiKey, String workspaceId) {

        this.workspaceId = workspaceId;

        BasicAuthRequestInterceptor basicAuthRequestInterceptor = new BasicAuthRequestInterceptor();
        basicAuthRequestInterceptor.setPassword(apiKey);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(basicAuthRequestInterceptor)
                .setEndpoint(ASANA_API_URL)
                .build();

        asana = restAdapter.create(Asana.class);
    }

    public void addProjectToCurrentlyAssignedIncompleteTasks(String projectId){

        //get list of assigned tasks
        List<TaskData.Task> tasks = asana.tasks("me", workspaceId, "now").data;

        tasks.stream().forEach(task -> {
            //for all assigned tasks add to the project
            asana.addProjectToTask(task.id, projectId);
            //add a comment
            asana.commentOnTask(task.id, "I have added to " + asana.project(projectId).data.name);
            //after adding to project remove from assignment
            asana.updateTask(task.id, "null");
        });
    }
}
