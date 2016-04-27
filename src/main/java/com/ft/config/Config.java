package com.ft.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    private String baseUrl;
    private String socialId;
    private String scheduledId;

    private List<String> authorizedTeams;
    private List<Map<String, String>> emailTeams;
    private List<TaskBot> bots;

    private Map<String, String> tags;


    @PostConstruct
    public void setupClient() {
        bots.forEach(TaskBot::setupClient);
    }
}
