package com.ft.asanaapi.model;

import com.asana.Client;
import com.asana.requests.CollectionRequest;
import com.asana.resources.Resource;
import com.ft.backup.model.BackupTask;

public class BackupTasksBase extends Resource {
    public BackupTasksBase(Client client) {
        super(client);
    }

    public CollectionRequest<BackupTask> findByProject(String projectId) {
        String path = String.format("/projects/%s/tasks", projectId);
        return new CollectionRequest<>(this, BackupTask.class, path, "GET");
    }
}
