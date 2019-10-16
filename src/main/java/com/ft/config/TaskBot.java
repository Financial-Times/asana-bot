package com.ft.config;

import com.asana.Client;
import com.ft.asanaapi.AsanaClientWrapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
@NoArgsConstructor
public class TaskBot {
    private String name;
    private String projectId;
    private String apiKey;
    private Integer runInterval;
    private AsanaClientWrapper client;
    private String runnerBean;
    private Map<String, String> tags;

    public TaskBot(String name, String projectId, String apiKey, AsanaClientWrapper client, String runnerBean,
                   Map<String, String> tags, Integer runInterval) {
        this.name = name;
        this.projectId = projectId;
        this.apiKey = apiKey;
        this.client = client;
        this.runnerBean = runnerBean;
        this.tags = tags;
        this.runInterval = runInterval;
    }

    public void setupClient(Map<String, String> tags, String workspaceId) {
        Client asanaClient = Client.accessToken(apiKey);
        asanaClient.headers.put("Asana-Disable", "string_ids");
        client = new AsanaClientWrapper(asanaClient, workspaceId);
        this.tags = tags;
    }

}
