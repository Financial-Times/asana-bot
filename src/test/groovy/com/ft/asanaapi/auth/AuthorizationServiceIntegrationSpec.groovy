package com.ft.asanaapi.auth
import com.ft.AsanaBot
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.charset.Charset

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

@IntegrationTest
@ContextConfiguration(classes = AsanaBot.class, loader = SpringApplicationContextLoader.class)
@ActiveProfiles("test")
class AuthorizationServiceIntegrationSpec extends Specification {

    @Autowired
    AuthorizationService authorizationService

    private static final String testWorkspaceId = "324300775153"
    private static final String TEST_USER_ID = "676767"
    private static final String BASIC_AUTH_HEADER = "Basic "

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8888))


    @Unroll
    void "authorize - success #scenario"() {
        given:
            String TEST_EMAIL = "test@ftqa.com"
            String ENCODED_TEST_EMAIL = encodeEmail(TEST_EMAIL)
            String responseFileSuffix = 'success'

            Map<String, String> authenticationDetails = [
                    email: TEST_EMAIL,
                    hd   : 'ftqa.com'
            ]
        and:
            stubGetUserByEmail(ENCODED_TEST_EMAIL, responseFileSuffix)
            stubGetUserTeams(TEST_USER_ID, userTeamsFile)
            defaultUnmatchedRequest()

        when:
            authorizationService.authorize(authenticationDetails)

        then:
            verifyGetUserByEmail(1, TEST_EMAIL)
            verifyGetUserTeams(1, TEST_USER_ID)
        and:
            authenticationDetails['ftAuthorized'] == expectedFtAuthorized
            authenticationDetails['teams'] == expectedTeams
        where:
            scenario                      | userTeamsFile | expectedFtAuthorized | expectedTeams
            'fully authorized user'       | 'success'     | 'true'               | 'Markets, World, Companies'
            'user not in authorized team' | 'failure'     | null                 | null

    }

    @Unroll
    void "authorize - failure #scenario"() {
        given:
            Map<String, String> authenticationDetails = [
                    email: email,
                    hd   : hostDomain
            ]
        and:
            stubGetUserByEmail(encodeEmail(email), userFile)
            stubGetUserTeams(TEST_USER_ID, 'success')
            defaultUnmatchedRequest()

        when:
            authorizationService.authorize(authenticationDetails)

        then:
            Exception caught = thrown(expectedException)
            caught.message == expectedMessage
            verifyGetUserByEmail(getUserByEmailCalls, email)
            verifyGetUserTeams(0, TEST_USER_ID)
        and:
            !authenticationDetails['ftAuthorized']
            !authenticationDetails['teams']

        where:
            scenario                              | email                | hostDomain    | getUserByEmailCalls | userFile    | expectedException      | expectedMessage
            'due to invalid domain'               | 'test@example.com'   | 'example.com' | 0                   | 'success'   | InvalidDomainException | 'Only FT account are authorized'
            'due to user not registered in Asana' | 'non.asana@ftqa.com' | 'ftqa.com'    | 1                   | 'non_asana' | NonAsanaUserException  | 'FT user is not registered for Asana'

    }

    private static String encodeEmail(String email) {
        URLEncoder.encode(email, Charset.defaultCharset().toString())
    }

    private stubGetUserByEmail(String email, String responseFile) {
        wireMockRule.stubFor(get(urlMatching("/api/1.0/workspaces/" + testWorkspaceId + "/typeahead\\?.*")).atPriority(1)
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("type", equalTo("user"))
                .withQueryParam("query", matching(email))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("auth/user_${responseFile}.json")))
    }

    private stubGetUserTeams(String userId, String responseFile) {
        wireMockRule.stubFor(get(urlMatching("/api/1.0/users/" + userId + "/teams\\?.*")).atPriority(1)
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("organization", matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("auth/user_teams_${responseFile}.json")))
    }

    private boolean verifyGetUserByEmail(int callsCount, String email) {
        wireMockRule.verify(callsCount, getRequestedFor(urlMatching("/api/1.0/workspaces/" + testWorkspaceId + "/typeahead\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("type", equalTo("user"))
                .withQueryParam("query", matching(email)))
        return true
    }

    private verifyGetUserTeams(int callsCount, String userId) {
        wireMockRule.verify(callsCount, getRequestedFor(urlMatching("/api/1.0/users/" + userId + "/teams\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("organization", matching(".*")))
        return true
    }

    private defaultUnmatchedRequest() {
        stubFor(get(urlMatching(".*")).atPriority(6) // default priority is 5
                .willReturn(aResponse()
                .withStatus(404)
                .withBody("Error in code somewhere")));
    }
}
