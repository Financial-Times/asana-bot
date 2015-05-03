package com.ft.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="asana")
public class Config {

    //From application.yml
    private static String graphicsId;
    private static String picturesId;
    private static String apiKey;

    public void setGraphicsId(String graphicsId){
        Config.graphicsId = graphicsId;
    }

    public static void setPicturesId(String picturesId) {
        Config.picturesId = picturesId;
    }

    public static String getPicturesId(){
        return picturesId;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        Config.apiKey = apiKey;
    }
}
