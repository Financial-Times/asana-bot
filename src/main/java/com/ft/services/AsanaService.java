package com.ft.services;

import com.ft.asanaapi.AsanaClient;
import com.ft.config.Config;
import org.springframework.stereotype.Service;

@Service
public class AsanaService {

    AsanaClient picturesAsanaClient;
    AsanaClient graphicsAsanaClient;

    AsanaService(){
        graphicsAsanaClient = new AsanaClient(System.getenv("ASANA_GRAPHICS_KEY"), Config.getWorkspace());
        picturesAsanaClient = new AsanaClient(System.getenv("ASANA_PICTURES_KEY"), Config.getWorkspace());
    }

    public void addGraphicsProjectToGraphicsBotAssignedTasks(){
        graphicsAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(Config.getGraphicsId());
    };

    public void addPicturesProjectToPicturesBotAssignedTasks(){
        picturesAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(Config.getPicturesId());
    };

}
