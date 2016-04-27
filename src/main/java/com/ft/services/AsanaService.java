package com.ft.services;

import com.ft.asanaapi.AsanaClient;
import com.ft.config.Config;
import com.ft.report.model.ReportTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import retrofit.client.Response;

import java.util.List;

@Service
@EnableConfigurationProperties(Config.class)
public class AsanaService {

    @Autowired private AsanaClient graphicsAsanaClient;
    @Autowired private AsanaClient socialAsanaClient;

    @Autowired private Config config;

    public void addGraphicsProjectToGraphicsBotAssignedTasks(){
        graphicsAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(config.getGraphicsId());
    }

    public void addSocialProjectToSocialBotAssignedTasks() {
        socialAsanaClient.addProjectToCurrentlyAssignedIncompleteTasks(config.getSocialId());
    }

    public List<ReportTask> findTasks(Long projectId, String completedSince) {
        return graphicsAsanaClient.findTaskItems(projectId, completedSince);
    }

    public Response ping() {
        return graphicsAsanaClient.ping();
    }


}
