package com.ft.services;

import com.ft.asanaapi.Asana;
import com.ft.asanaapi.AsanaClient;
import com.ft.config.Config;
import org.springframework.stereotype.Service;

@Service
public class AsanaService {

    AsanaClient picturesAsanaClient;
    AsanaClient graphicsAsanaClient;

    public AsanaService(){
        this(Config.getWorkspace(), Asana.ASANA_API_URL);
    }

    public AsanaService(String workspaceId, String baseUrl){
        graphicsAsanaClient = new AsanaClient(System.getenv("ASANA_GRAPHICS_KEY"), workspaceId, baseUrl);
        picturesAsanaClient = new AsanaClient(System.getenv("ASANA_PICTURES_KEY"), workspaceId, baseUrl);
    }

    public void addGraphicsProjectToGraphicsBotAssignedTasks(){
        addGraphicsProjectToGraphicsBotAssignedTasks(Config.getGraphicsId());
    }

    public void addGraphicsProjectToGraphicsBotAssignedTasks(String projectId){
        graphicsAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(projectId);
    }

    public void addPicturesProjectToPicturesBotAssignedTasks(){
        //picturesAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(Config.getPicturesId());
    }
}
