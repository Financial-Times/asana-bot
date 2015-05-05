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
//        String assignee;
//        String assignee_status;
//        String created_at;
//        String completed;
//        String completed_at;
//        String due_on;
//        String due_at;
//        String external;
//        String followers;
//        String hearted;
//        String hearts;
//        String modified_at;
        String name;
//        String notes;
//        String num_hearts;
//        String projects;
//        String parent;
//        String workspace;
//        String memberships;

        @Override
        public String toString() {
            return "id: " + id + " , name: " + name;
        }

//        public boolean isComplete(){
//            if (completed != null && completed.equals("false")){
//                return false;
//            } else {
//                return true;
//            }
//        }
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
        tasks.stream().forEach(task -> asana.updateTask(task.id, "null"));
    }
}
