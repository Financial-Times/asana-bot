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
    private static String workspace;
    private static String graphicsId;
    private static String picturesId;

    //GETTERS AND SETTERS
    public static String getPicturesId() {
        return picturesId;
    }

    public static void setPicturesId(String picturesId) {
        Config.picturesId = picturesId;
    }

    public static String getGraphicsId() {
        return graphicsId;
    }

    public void setGraphicsId(String graphicsId) {
        Config.graphicsId = graphicsId;
    }

    public static String getWorkspace() {
        return workspace;
    }

    public static void setWorkspace(String workspace) {
        Config.workspace = workspace;
    }
}
