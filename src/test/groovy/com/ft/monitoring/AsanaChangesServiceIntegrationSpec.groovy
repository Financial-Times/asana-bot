package com.ft.monitoring

import com.ft.test.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Stepwise

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.containing
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching

@Stepwise
class AsanaChangesServiceIntegrationSpec extends IntegrationSpec {

    @Autowired private AsanaChangesService asanaEventsService

    private static final String ENCODED_OPT_EXPAND = "this"

    void 'initial request populates previous state and returns returns no changes'() {
        given:
            stubGetEventsFirstRequest('initial_request.json')

        when:
            List<ProjectChange> projectChanges = asanaEventsService.getChanges()

        then:
            verifyGetEventsFirstRequest()
        and:
            !projectChanges
    }

    void 'subsequent request compares previous with current state of projects'() {
        given:
            wireMockRule.resetMappings()
            stubGetEventsFirstRequest('subsequent_request.json')

        when:
            List<ProjectChange> projectChanges = asanaEventsService.getChanges()

        then:
            verifyGetEventsFirstRequest()
        and:
            projectChanges
            projectChanges.size() == 4
        and:
            projectChanges[0].getProject().getId() == '37532256694653'
            projectChanges[0].getChangeTypes() == [ChangeType.NAME]
            projectChanges[1].getProject().getId() == '39486514321993'
            projectChanges[1].getChangeTypes() == [ChangeType.ARCHIVED]
            projectChanges[2].getProject().getId() == '37532256694667'
            projectChanges[2].getChangeTypes() == [ChangeType.TEAM]
            projectChanges[3].getProject().getId() == '37532256694668'
            projectChanges[3].getChangeTypes() == [ChangeType.NAME, ChangeType.ARCHIVED, ChangeType.TEAM]

    }

    private void stubGetEventsFirstRequest(String fileName) {
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

    private  boolean verifyGetEventsFirstRequest() {
        wireMockRule.verify(1, getRequestedFor(urlMatching("/api/1.0/projects\\?.*"))
                .withQueryParam("workspace", equalTo('324300775153'))
                .withQueryParam("opt_expand", equalTo(ENCODED_OPT_EXPAND)))
        return true
    }

}
