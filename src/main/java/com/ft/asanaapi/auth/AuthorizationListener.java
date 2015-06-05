package com.ft.asanaapi.auth;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthorizationListener implements ApplicationListener<AuthorizedEvent> {

    @Autowired @Setter
    private AuthorizationService authorizationService;

    @Override
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(final AuthorizedEvent auditApplicationEvent) {

        Authentication auth = auditApplicationEvent.getAuthentication();
        if (auth instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) auth;
            Map<String, String> details = (Map) oAuth2Authentication.getUserAuthentication().getDetails();
            authorizationService.authorize(details);
        }
    }
}
