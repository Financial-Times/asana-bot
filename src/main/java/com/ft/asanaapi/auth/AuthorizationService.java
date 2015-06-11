package com.ft.asanaapi.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthorizationService {

    public static final String HOSTED_DOMAIN = "hd";
    public static final String EMAIL = "email";

    public static final String FT_AUTHORIZED = "ftAuthorized";
    public static final String TEAMS = "teams";

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    @Autowired private DomainValidator domainValidator;
    @Autowired private TeamValidator teamValidator;

    public void authorize(Map<String, String> authenticationDetails) throws UsernameNotFoundException {
        String email = authenticationDetails.get(EMAIL);
        String domain = authenticationDetails.get(HOSTED_DOMAIN);
        logger.debug("Authorizing user: " + email);

        domainValidator.validate(email, domain);
        List<String> teamNames = teamValidator.validate(email);
        if (isUserAuthorized(teamNames)) {
            grantAuthority(authenticationDetails, teamNames);
        }
    }

    private boolean isUserAuthorized(List<String> teams) {
        return teams.size() > 0;
    }

    private void grantAuthority(Map<String, String> authenticationDetails, List<String> teams) {
        authenticationDetails.put(FT_AUTHORIZED, Boolean.TRUE.toString());
        String formattedTeams = teams.stream().collect(Collectors.joining(", "));
        authenticationDetails.put(TEAMS, formattedTeams);
    }
}
