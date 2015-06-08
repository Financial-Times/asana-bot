package com.ft.asanaapi.auth;

import com.ft.asanaapi.AsanaClient;
import com.ft.asanaapi.model.Team;
import com.ft.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeamValidator {

    private static final Logger logger = LoggerFactory.getLogger(TeamValidator.class);

    @Autowired private AsanaClient graphicsAsanaClient;
    @Autowired private Config config;

    public List<String> validate(String email) {
        List<Team> teams = graphicsAsanaClient.findTeams(email);
        if(teams == null || teams.size() == 0) {
            logger.warn("Authorization failed due to user not assigned to editorial team - user: " + email);
            return Collections.emptyList();
        }
        return filterAuthorizedTeams(teams);
    }

    private List<String> filterAuthorizedTeams(List<Team> teams) {
        return teams.stream()
                .map(Team::getName)
                .filter(teamName -> config.getAuthorizedTeams().contains(teamName))
                .collect(Collectors.toList());
    }
}
