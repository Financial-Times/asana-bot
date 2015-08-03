package com.ft.services;

import java.util.List;

import com.ft.asanaapi.AsanaClient;
import com.ft.config.Config;
import com.ft.report.model.ReportTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import retrofit.client.Response;

@Service
@EnableConfigurationProperties(Config.class)
public class AsanaService {

    @Autowired private AsanaClient picturesAsanaClient;
    @Autowired private AsanaClient graphicsAsanaClient;
    @Autowired private AsanaClient socialAsanaClient;
    @Autowired private AsanaClient interactivesAsanaClient;
    @Autowired private AsanaClient videoAsanaClient;

    @Autowired private Config config;

    public void addGraphicsProjectToGraphicsBotAssignedTasks(){
        graphicsAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(config.getGraphicsId());
    }

    public void addPicturesProjectToPicturesBotAssignedTasks(){
        picturesAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(config.getPicturesId());
    }

    public List<ReportTask> findTasks(String projectId, String completedSince) {
        return graphicsAsanaClient.findTaskItems(projectId, completedSince);
    }

    public Response ping() {
        return graphicsAsanaClient.ping();
    }


    public void addSocialProjectToSocialBotAssignedTasks() {
        socialAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(config.getSocialId());
    }

    public void addInteractivesProjectToInteractivesBotAssignedTasks() {
        interactivesAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(config.getInteractivesId());
    }

    public void addVideoProjectToVideoBotAssignedTasks() {
        videoAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(config.getVideoId());
    }
}
