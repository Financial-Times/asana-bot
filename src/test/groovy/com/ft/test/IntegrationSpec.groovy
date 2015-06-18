package com.ft.test

import com.ft.AsanaBot
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.Rule
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

@IntegrationTest
@ContextConfiguration(classes = AsanaBot.class, loader = SpringApplicationContextLoader.class)
@ActiveProfiles("test")
class IntegrationSpec extends Specification {

    protected static final String testWorkspaceId = "324300775153"
    protected static final String BASIC_AUTH_HEADER = "Basic "
    protected static final String APPLICATION_JSON_CONTENT_TYPE = "application/json"

    @Rule
    protected final WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8888))

}
