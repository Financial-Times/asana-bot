package com.ft.asanaapi.auth

import com.ft.asanaapi.AsanaClient
import com.ft.asanaapi.model.Team
import com.ft.config.Config
import org.junit.Rule
import org.springframework.boot.test.OutputCapture
import spock.lang.Specification

class TeamValidatorSpec extends Specification {

    public static final String TEST_EMAIL = 'test@example.com'
    private TeamValidator validator
    private AsanaClient mockGraphicsAsanaClient
    private Config mockConfig

    @Rule OutputCapture capture = new OutputCapture()

    void setup() {
        validator = new TeamValidator()

        mockGraphicsAsanaClient = Mock(AsanaClient)
        validator.graphicsAsanaClient = mockGraphicsAsanaClient

        mockConfig = Mock(Config)
        validator.config = mockConfig

        capture.flush()
    }

    void "validate - success"() {
        given:
            Team team = new Team(name: 'test team')
            List<Team> teams = [team]
            List<String> configuredAuthorizedTeams = [team.name]

        when:
            List<String> authorizedTeams = validator.validate(TEST_EMAIL)

        then:
            1 * mockGraphicsAsanaClient.findTeams(TEST_EMAIL) >> teams
            1 * mockConfig.getAuthorizedTeams() >> configuredAuthorizedTeams
            0 * _
        and:
            authorizedTeams == configuredAuthorizedTeams
    }

    void "validate - failure - user not assigned to any team"() {
        when:
            List<String> authorizedTeams = validator.validate(TEST_EMAIL)

        then:
            1 * mockGraphicsAsanaClient.findTeams(TEST_EMAIL) >> []
            0 * _
        and:
            authorizedTeams == []
        and:
            capture.toString().contains("Authorization failed due to user not assigned to any team - user: " + TEST_EMAIL)
    }

    void "validate - failure - user not assigned to authorized team"() {
        given:
            Team team = new Team(name: 'test team')
            List<Team> userTeams = [team]
            List<String> configuredAuthorizedTeams = ['another team']

        when:
            List<String> authorizedTeams = validator.validate(TEST_EMAIL)

        then:
            1 * mockGraphicsAsanaClient.findTeams(TEST_EMAIL) >> userTeams
            1 * mockConfig.getAuthorizedTeams() >> configuredAuthorizedTeams
            0 * _
        and:
            authorizedTeams == []
        and:
            capture.toString().contains("Authorization failed due to user not assigned to any of authorized team - user: " + TEST_EMAIL)
    }
}
