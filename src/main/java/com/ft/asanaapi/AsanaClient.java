package com.ft.asanaapi;

import com.ft.asanaapi.auth.BasicAuthRequestInterceptor;
import com.ft.asanaapi.model.Task;
import com.ft.asanaapi.model.TasksData;
import retrofit.RestAdapter;

import java.util.List;

public class AsanaClient {

    private String workspaceId;
    private Asana asana;

    public AsanaClient(String apiKey, String workspaceId, String baseUrl) {

        this.workspaceId = workspaceId;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor((new BasicAuthRequestInterceptor()).setPassword(apiKey))
                .setEndpoint(baseUrl)
                .build();

        asana = restAdapter.create(Asana.class);
    }

    public void addProjectToCurrentlyAssignedIncompleteTasks(String projectId){

        //get list of assigned tasks
        TasksData tasksData = asana.tasks("me", workspaceId, "now","id,name,parent.id,parent.name");


        System.out.println(tasksData);

        List<Task> tasks = tasksData.data;


        tasks.stream().forEach(task -> {
            //for all assigned tasks add to the project
            asana.addProjectToTask(task.id, projectId);
            //add a comment to the parent task if one exists
            if (task.parent != null) {
                asana.commentOnTask(task.parent.id, "I have added the task "
                        + task.name + " to " + asana.project(projectId).data.name);
            }
            //after adding to project remove from assignment
            asana.updateTask(task.id, "null");
        });
    }
}
