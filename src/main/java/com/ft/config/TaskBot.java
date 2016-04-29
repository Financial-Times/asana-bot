package com.ft.config;

import com.asana.Client;
import com.ft.asanaapi.AsanaClientWrapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Getter @Setter
@NoArgsConstructor
public class TaskBot {
    private String name;
    private String projectId;
    private String apiKey;
    private AsanaClientWrapper client;
    private String runnerBean;
    private Map<String, String> tags;
    private String workspaceId;

    private static final Logger logger = LoggerFactory.getLogger(TaskBot.class);

    public TaskBot(String name, String projectId, String apiKey, AsanaClientWrapper client, String runnerBean, String workspaceId, Map<String, String> tags) {
        this.name = name;
        this.projectId = projectId;
        this.apiKey = apiKey;
        this.client = client;
        this.runnerBean = runnerBean;
        this.workspaceId = workspaceId;
        this.tags = tags;
    }

    public void setupClient(Map<String, String> tags, String workspaceId) {
        Client asanaClient = Client.accessToken(apiKey);
        client = new AsanaClientWrapper(asanaClient);

        this.tags = tags;
        this.workspaceId = workspaceId;
    }

}
