package com.ft.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * A class to hold configuration properties for various projects and static
 * settings that the bot interacts with.
 */
@Component
@ConfigurationProperties(prefix = "asana")
@Getter @Setter @NoArgsConstructor
public class Config {

    //From application.yml
    private String workspace;
    private String graphicsId;
    private String picturesId;
    private String designId;
    private String baseUrl;
    private String socialId;

    private List<String> authorizedTeams;
    private List<Map<String, String>> emailTeams;

    private Map<String, String> tags;
}
