package com.ft.asanaapi.model;

import com.asana.Client;
import com.asana.requests.CollectionRequest;
import com.asana.resources.Resource;
import com.ft.report.model.ReportTask;

public class ReportTasksBase extends Resource {
    public ReportTasksBase(Client client) {
        super(client);
    }

    public CollectionRequest<ReportTask> findByProject(String projectId) {
        String path = String.format("/projects/%s/tasks", projectId);
        return new CollectionRequest<>(this, ReportTask.class, path, "GET");
    }
}
