package com.ft.boot

import com.asana.errors.AsanaError
import com.ft.services.AsanaService
import com.google.api.client.http.HttpResponseException
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.Status
import spock.lang.Specification
import spock.lang.Unroll

class AsanaHealthIndicatorSpec extends Specification {

    private AsanaHealthIndicator asanaHealthIndicator
    private AsanaService mockAsanaService

    void setup() {
        mockAsanaService = Mock(AsanaService)
        asanaHealthIndicator = new AsanaHealthIndicator(mockAsanaService)
    }

    void "health - ok"() {
        when:
            Health health = asanaHealthIndicator.health()

        then:
            1 * mockAsanaService.ping()
            0 * _
        and:
            health.status == Status.UP
    }

    @Unroll
    void "health - scenario: #scenario"() {
        when:
            Health health = asanaHealthIndicator.health()

        then:
            1 * mockAsanaService.ping() >> { throw exception }
            0 * _
        and:
            health.status == Status.DOWN
            health.details == expectedDetails

        where:
            scenario      | exception                                                            | expectedDetails
            'IOException' | new IOException("test exception")                                    | [asanaStatus: 500, asanaResponse: 'test exception']
            'AsanaError'  | new AsanaError("test Asana Error", 404, Mock(HttpResponseException)) | [asanaStatus: 404, asanaResponse: 'test Asana Error']

    }

}
