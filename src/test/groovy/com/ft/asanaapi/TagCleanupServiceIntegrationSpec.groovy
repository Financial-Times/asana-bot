package com.ft.asanaapi

import com.ft.AsanaBot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@IntegrationTest
@ContextConfiguration(classes = AsanaBot.class, loader = SpringApplicationContextLoader.class)
@ActiveProfiles("production")
class TagCleanupServiceIntegrationSpec extends Specification {

    @Autowired TagCleanupService tagCleanupService

    void 'cleanup tags with report'() {
        when:
            CleanupReport cleanupReport = tagCleanupService.cleanup()
        then:
            cleanupReport
            println cleanupReport
    }
}
