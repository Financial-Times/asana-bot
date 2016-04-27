package com.ft.services;

import com.ft.asanaapi.AsanaClient;
import com.ft.report.model.ReportTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit.client.Response;

import java.util.List;

@Service
@Deprecated //graphicsAsanaClient should be removed
public class AsanaService {

    @Autowired private AsanaClient graphicsAsanaClient;

    @Deprecated //See AsanaClientWrapper
    public List<ReportTask> findTasks(Long projectId, String completedSince) {
        return graphicsAsanaClient.findTaskItems(projectId, completedSince);
    }

    @Deprecated //See AsanaClientWrapper
    public Response ping() {
        return graphicsAsanaClient.ping();
    }


}
