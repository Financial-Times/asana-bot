package com.ft.asanaapi.auth

import org.springframework.security.access.event.AuthorizedEvent
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.OAuth2Authentication
import spock.lang.Specification

class AuthorizationListenerSpec extends Specification {

    AuthorizationListener authorizationListener
    AuthorizationService mockAuthorizationService

    void setup() {
        authorizationListener = new AuthorizationListener()
        mockAuthorizationService = Mock(AuthorizationService)
        authorizationListener.authorizationService = mockAuthorizationService
    }

    void "onApplicationEvent"() {
        given:
            AuthorizedEvent auditApplicationEvent = Mock(AuthorizedEvent)
            OAuth2Authentication mockOAuth2Authentication = Mock(OAuth2Authentication)
            Authentication mockAuthentication = Mock(Authentication)
            Map<String, String> details = [:]

        when:
            authorizationListener.onApplicationEvent(auditApplicationEvent)

        then:
            1 * auditApplicationEvent.getAuthentication() >> mockOAuth2Authentication
            1 * mockOAuth2Authentication.getUserAuthentication() >> mockAuthentication
            1 * mockAuthentication.getDetails() >> details
            1 * mockAuthorizationService.authorize(details)
            0 * _
    }
}
