package com.ft.asanaapi;

import com.ft.asanaapi.auth.BasicAuthRequestInterceptor;
import com.ft.asanaapi.model.Task;
import com.ft.asanaapi.model.TasksData;
import com.ft.config.Config;
import retrofit.RestAdapter;

import java.util.List;

public class AsanaClient {

    private Config config;
    private Asana asana;

    public AsanaClient(String apiKey, Config config) {

        this.config = config;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor((new BasicAuthRequestInterceptor()).setPassword(apiKey))
                .setEndpoint(config.getBaseUrl())
                .build();

        asana = restAdapter.create(Asana.class);
    }

    public void addProjectToCurrentlyAssignedIncompleteTasks(String projectId){

        //get list of assigned tasks
        TasksData tasksData = asana.tasks("me", config.getWorkspace(), "now","id,name,parent.id,parent.name");


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
