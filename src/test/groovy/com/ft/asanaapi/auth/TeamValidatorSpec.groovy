package com.ft.asanaapi.auth

import com.asana.models.Team
import com.asana.models.User
import com.ft.asanaapi.AsanaClientWrapper
import com.ft.config.Config
import org.junit.Rule
import org.springframework.boot.test.OutputCapture
import spock.lang.Specification

class TeamValidatorSpec extends Specification {

    private static final String TEST_EMAIL = 'test@example.com'
    private static final User USER = new User(id: '2643', name: 'test user')
    private static final Optional<User> OPTIONAL_USER = Optional.of(USER)
    private TeamValidator validator
    private AsanaClientWrapper mockAsanaClientWrapper
    private Config mockConfig

    @Rule OutputCapture capture = new OutputCapture()

    void setup() {
        mockAsanaClientWrapper = Mock(AsanaClientWrapper)
        mockConfig = Mock(Config)
        validator = new TeamValidator(mockAsanaClientWrapper, mockConfig)
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
            1 * mockAsanaClientWrapper.findUsersByWorkspace(TEST_EMAIL) >> OPTIONAL_USER
            1 * mockAsanaClientWrapper.getUserTeams(USER.id) >> teams
            1 * mockConfig.getAuthorizedTeams() >> configuredAuthorizedTeams
            0 * _
        and:
            authorizedTeams == configuredAuthorizedTeams
    }

    void "validate - failure - user not assigned to any team"() {
        when:
            List<String> authorizedTeams = validator.validate(TEST_EMAIL)

        then:
            1 * mockAsanaClientWrapper.findUsersByWorkspace(TEST_EMAIL) >> OPTIONAL_USER
            1 * mockAsanaClientWrapper.getUserTeams(USER.id) >> []
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
            1 * mockAsanaClientWrapper.findUsersByWorkspace(TEST_EMAIL) >> OPTIONAL_USER
            1 * mockAsanaClientWrapper.getUserTeams(USER.id) >> userTeams
            1 * mockConfig.getAuthorizedTeams() >> configuredAuthorizedTeams
            0 * _
        and:
            authorizedTeams == []
        and:
            capture.toString().contains("Authorization failed due to user not assigned to any of authorized team - user: " + TEST_EMAIL)
    }
}
