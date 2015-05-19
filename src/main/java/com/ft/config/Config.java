package com.ft.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * A class to hold configuration properties for various projects and static
 * settings that the bot interacts with.
 */
@Component
@ConfigurationProperties(prefix = "asana")
public class Config {

    //From application.yml
    private String workspace;
    private String graphicsId;
    private String picturesId;
    private String baseUrl;

    //GETTERS AND SETTERS
    public String getPicturesId() {
        return picturesId;
    }

    public void setPicturesId(String picturesId) {
        this.picturesId = picturesId;
    }

    public String getGraphicsId() {
        return graphicsId;
    }

    public void setGraphicsId(String graphicsId) {
        this.graphicsId = graphicsId;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
