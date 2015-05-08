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

    static class Data {
        List<Task> data;
    }

    static class EmptyData {
    }

    static class Task {
        String id;
        String name;

        @Override
        public String toString() {
            return "id: " + id + " , name: " + name;
        }
    }

    interface Asana {
        @GET("/tasks")
        Data tasks(
                @Query("assignee") String assignee,
                @Query("workspace") String workspace,
                @Query("completed_since") String completedSince
        );

        @FormUrlEncoded
        @POST("/tasks/{task-id}/addProject")
        EmptyData addProjectToTask(
                @Path("task-id") String taskId,
                @Field("project") String projectId
        );

        @FormUrlEncoded
        @PUT("/tasks/{task-id}")
        Data updateTask(
                @Path("task-id") String taskId,
                @Field("assignee") String assignee
        );

        @FormUrlEncoded
        @POST("/tasks/{task-id}/stories")
        Data commentOnTask(
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

    public void addProjectToCurrentlyAssignedUncompleteTasks(String projectId){

        //get list of assigned tasks
        List<Task> tasks = asana.tasks("me", workspaceId, "now").data;

        //for all assigned tasks add to the project
        tasks.stream().forEach(task -> asana.addProjectToTask(task.id, projectId));

        //after adding to project remove from assignment
        tasks.stream().forEach(task -> {
            asana.commentOnTask(task.id, "Added to Project");
            asana.updateTask(task.id, "null");
        });
    }
}
