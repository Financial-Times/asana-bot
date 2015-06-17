package com.ft.boot

import com.ft.test.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.Status
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.*

class AsanaHealthIndicatorIntegrationSpec extends IntegrationSpec {

    @Autowired
    AsanaHealthIndicator asanaHealthIndicator

    @Unroll
    void "health - scenario: #scenario"() {
        given:
            stubPing(httpStatus)

        when:
            Health health = asanaHealthIndicator.health()

        then:
            verifyPing()
        and:
            health.status == expectedStatus

        where:
            scenario               | httpStatus | expectedStatus
            'workspace found'      | 200        | Status.UP
            'bad request'          | 400        | Status.DOWN
            'internal Asana error' | 500        | Status.DOWN

    }

    private stubPing(int httpStatus) {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/1.0/workspaces/" + testWorkspaceId))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .willReturn(aResponse()
                .withStatus(httpStatus)))
    }

    private boolean verifyPing() {
        wireMockRule.verify(1, getRequestedFor(urlMatching("/api/1.0/workspaces/" + testWorkspaceId))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER)))
        return true
    }

}
