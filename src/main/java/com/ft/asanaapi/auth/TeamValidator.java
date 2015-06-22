package com.ft.asanaapi.auth;

import com.ft.asanaapi.AsanaClient;
import com.ft.asanaapi.model.Team;
import com.ft.config.Config;
import lombok.Setter;
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
    private static final List<String> NO_TEAMS = Collections.emptyList();

    @Autowired @Setter private AsanaClient graphicsAsanaClient;
    @Autowired @Setter private Config config;

    public List<String> validate(String email) {
        List<Team> teams = graphicsAsanaClient.findTeams(email);

        if(teams == null || teams.size() == 0) {
            logger.debug("Authorization failed due to user not assigned to any team - user: " + email);
            return NO_TEAMS;
        }
        List<String> authorizedTeams = filterAuthorizedTeams(teams);
        if(authorizedTeams == null || authorizedTeams.size() == 0) {
            logger.debug("Authorization failed due to user not assigned to any of authorized team - user: " + email);
            return NO_TEAMS;
        }
        return authorizedTeams;
    }

    private List<String> filterAuthorizedTeams(List<Team> teams) {
        return teams.stream()
                .map(Team::getName)
                .filter(teamName -> config.getAuthorizedTeams().contains(teamName))
                .collect(Collectors.toList());
    }
}
