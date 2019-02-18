package com.ft.asanaapi.model;

import com.asana.Client;
import com.asana.requests.CollectionRequest;
import com.asana.resources.Resource;

public class CustomTasksBase extends Resource {
    public CustomTasksBase(Client client) {
        super(client);
    }

    public CollectionRequest<CustomTask> findByProject(String projectId) {
        String path = String.format("/projects/%s/tasks", projectId);
        return new CollectionRequest<>(this, CustomTask.class, path, "GET");
    }

}
