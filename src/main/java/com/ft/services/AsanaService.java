package com.ft.services;

import com.ft.asanaapi.AsanaClient;
import com.ft.asanaapi.AsanaClientWrapper;
import com.ft.config.Config;
import com.ft.report.model.ReportTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AsanaService {

     private AsanaClient graphicsAsanaClient;
     private AsanaClientWrapper defaultAsanaClientWrapper;
     private Config config;

    @Autowired
    public AsanaService(AsanaClient graphicsAsanaClient, AsanaClientWrapper defaultAsanaClientWrapper, Config config) {
        this.graphicsAsanaClient = graphicsAsanaClient;
        this.defaultAsanaClientWrapper = defaultAsanaClientWrapper;
        this.config = config;
    }

    @Deprecated //See AsanaClientWrapper
    public List<ReportTask> findTasks(Long projectId, String completedSince) {
        return graphicsAsanaClient.findTaskItems(projectId, completedSince);
    }

    public void ping() throws IOException {
        defaultAsanaClientWrapper.getWorkspace(config.getWorkspace());
    }


}
