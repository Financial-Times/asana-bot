package com.ft.config;

import com.asana.Client;
import com.ft.asanaapi.AsanaClientWrapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TaskBot {
    private String name;
    private String projectId;
    private String apiKey;
    private AsanaClientWrapper client;

    public TaskBot(String name, String projectId, String apiKey, AsanaClientWrapper client) {
        this.name = name;
        this.projectId = projectId;
        this.apiKey = apiKey;
        this.client = client;
    }

    public void setupClient() {
        Client asanaClient = Client.accessToken(apiKey);
        client = new AsanaClientWrapper(asanaClient);
    }
}
