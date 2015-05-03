package com.ft.services;

import com.ft.config.Config;
import net.joelinn.asana.Asana;
import net.joelinn.asana.projects.Project;
import net.joelinn.asana.tasks.Task;
import net.joelinn.asana.tasks.Tasks;
import net.joelinn.asana.tasks.TasksClient;
import net.joelinn.asana.workspaces.Workspace;
import net.joelinn.asana.workspaces.Workspaces;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsanaService {

    //@Value("#{systemProperties.ASANA_KEY}")
    private String apiKey;

    private Asana asana;
    private TasksClient tasksClient;

    AsanaService(){
        //asana = new Asana(apiKey);
        asana = new Asana(Config.getApiKey());
        //tasksClient = new TasksClient(Config.getApiKey());
    }



    public String getAssignedTasks(){
        return Config.getPicturesId();
    }

    public void addAssignedTasksToPictures(){
        //Get Assigned Tasks
        //List<Task> tasks = asana.workspaces().getWorkspaces();
        //tasksClient.getTasks();
//        for(Task task : tasks){
//            System.out.println(task.name);
//        }
        //tasks.forEach(task -> System.out.println(task.name));

        List<Workspace> workspaces = asana.workspaces().getWorkspaces();
        workspaces.forEach(workspace -> {
            System.out.println(workspace.id);
            List<Project> projects = asana.workspaces().getProjects(workspace.id);
            projects.forEach(project -> {
                        System.out.println(project.name);
                        List<Task> tasks = asana.projects().getTasks(project.id);
                        tasks.forEach(task -> {
                            System.out.println(task.name);
                            System.out.println((task.assignee != null)? task.assignee.email : task.assigneeStatus);
                        });
                    }
            );
        });
    }
}
