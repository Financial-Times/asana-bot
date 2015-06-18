package com.ft.asanaapi.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AuthorizationService {

    private static final String HOSTED_DOMAIN = "hd";
    private static final String EMAIL = "email";

    private static final String FT_AUTHORIZED = "ftAuthorized";
    private static final String TEAMS = "teams";

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    @Autowired private DomainValidator domainValidator;
    @Autowired private TeamValidator teamValidator;

    public void authorize(Map<String, Object> authenticationDetails) throws UsernameNotFoundException {
        if (isAlreadyAuthorized(authenticationDetails)) {
            return;
        }

        String email = (String) authenticationDetails.get(EMAIL);
        String domain = (String) authenticationDetails.get(HOSTED_DOMAIN);
        logger.debug("Authorizing user: " + email);

        domainValidator.validate(email, domain);
        List<String> teamNames = teamValidator.validate(email);
        if (isUserAuthorized(teamNames)) {
            grantAuthority(authenticationDetails, teamNames);
        }
    }

    private boolean isAlreadyAuthorized(Map<String, Object> authenticationDetails) {
        String authorizationFlag = (String) authenticationDetails.get(FT_AUTHORIZED);
        return Boolean.TRUE.toString().equals(authorizationFlag);

    }

    private boolean isUserAuthorized(List<String> teams) {
        return teams.size() > 0;
    }

    private void grantAuthority(Map<String, Object> authenticationDetails, List<String> teams) {
        authenticationDetails.put(FT_AUTHORIZED, Boolean.TRUE.toString());
        authenticationDetails.put(TEAMS, teams);
    }
}
