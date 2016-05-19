package com.ft.asanaapi.auth;

import com.asana.models.Team;
import com.asana.models.User;
import com.ft.asanaapi.AsanaClientWrapper;
import com.ft.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TeamValidator {

    private static final Logger logger = LoggerFactory.getLogger(TeamValidator.class);
    private static final List<String> NO_TEAMS = Collections.emptyList();

    private AsanaClientWrapper asanaClientWrapper;
    private Config config;

    @Autowired
    public TeamValidator(AsanaClientWrapper asanaClientWrapper, Config config) {
        this.asanaClientWrapper = asanaClientWrapper;
        this.config = config;
    }

    public List<String> validate(String email) {
        Optional<User> user;
        List<Team> teams = null;
        try {
            user = asanaClientWrapper.findUsersByWorkspace(email);
            if (user.isPresent()) {
                teams = asanaClientWrapper.getUserTeams(user.get().id);
            } else {
                throw new NonAsanaUserException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                .map(team -> team.name)
                .filter(teamName -> config.getAuthorizedTeams().contains(teamName))
                .collect(Collectors.toList());
    }
}
