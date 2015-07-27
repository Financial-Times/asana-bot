package com.ft.monitoring

import com.ft.test.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.containing
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching

class AsanaChangesServiceIntegrationSpec extends IntegrationSpec {

    @Autowired private AsanaChangesService asanaEventsService

    private static final String ENCODED_OPT_EXPAND = "this"

    void 'no changes'() {
        given:
            stubGetProjectsRequest('no_changes.json')

        when:
            List<ProjectChange> projectChanges = asanaEventsService.getChanges()

        then:
            verifyGetProjectsRequest()
        and:
            !projectChanges
    }

    void 'all possible changes'() {
        given:
            wireMockRule.resetMappings()
            stubGetProjectsRequest('all_changes.json')

        when:
            List<ProjectChange> projectChanges = asanaEventsService.getChanges()

        then:
            verifyGetProjectsRequest()
        and:
            projectChanges
            projectChanges.size() == 4
        and:
            projectChanges[0].getProject().getId() == '12345'
            projectChanges[0].getChanges() == [new Change('Companies Topics', 'Project 1 was renamed', ChangeType.NAME)]
            projectChanges[1].getProject().getId() == '23456'
            projectChanges[1].getChanges() == [new Change('false', 'true', ChangeType.ARCHIVED)]
            projectChanges[2].getProject().getId() == '9876'
            projectChanges[2].getChanges() == [new Change('Lex', 'Companies', ChangeType.TEAM)]
            projectChanges[3].getProject().getId() == '379876'
            projectChanges[3].getChanges() == [new Change('Project 4', 'Project 4 was renamed', ChangeType.NAME),
                                               new Change('false', 'true', ChangeType.ARCHIVED),
                                               new Change('Team 4', 'Team 1', ChangeType.TEAM)]

    }

    void 'project not found'() {
        given:
            wireMockRule.resetMappings()
            stubGetProjectsRequest('not_found.json')

        when:
            List<ProjectChange> projectChanges = asanaEventsService.getChanges()

        then:
            verifyGetProjectsRequest()
        and:
            projectChanges
            projectChanges.size() == 4
        and:
            projectChanges[0].getReferenceProject().getName() == 'Companies Topics'
            projectChanges[0].getChanges() == [new Change('Companies Topics', null, ChangeType.NOT_FOUND)]
            projectChanges[1].getReferenceProject().getName() == 'World Topics'
            projectChanges[1].getChanges() == [new Change('World Topics', null, ChangeType.NOT_FOUND)]
            projectChanges[2].getReferenceProject().getName() == 'Lex Topics'
            projectChanges[2].getChanges() == [new Change('Lex Topics', null, ChangeType.NOT_FOUND)]
            projectChanges[3].getReferenceProject().getName() == 'Project 4'
            projectChanges[3].getChanges() == [new Change('Project 4', null, ChangeType.NOT_FOUND)]
    }

    private void stubGetProjectsRequest(String fileName) {
        wireMockRule.stubFor(get(urlMatching("/api/1.0/projects\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("workspace", equalTo('324300775153'))
                .withQueryParam("opt_expand", equalTo(ENCODED_OPT_EXPAND))
                .willReturn(
                    aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_CONTENT_TYPE)
                    .withBodyFile("monitoring/${fileName}")))
    }

    private  boolean verifyGetProjectsRequest() {
        wireMockRule.verify(1, getRequestedFor(urlMatching("/api/1.0/projects\\?.*"))
                .withQueryParam("workspace", equalTo('324300775153'))
                .withQueryParam("opt_expand", equalTo(ENCODED_OPT_EXPAND)))
        return true
    }

}
