package com.ft.asanaapi;

import com.ft.AsanaBot;
import com.ft.services.AsanaService;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AsanaBot.class)
@ActiveProfiles("test")
public class AsanaServiceIntegrationTest {


    private static final String testWorkspaceId = "324300775153";
    private static final String encodedOptFields = "id%2Cname%2Cparent.id%2Cparent.name";

    public static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    public static final String APPLICATION_FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String BASIC_AUTH_HEADER = "Basic OVBGeG1DaXkuNVpzUVBJWHZFcXU2Qjk2emY1QlRlQjU6";

    @Autowired
    private AsanaService asanaService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8888));

    @Test
    public void exampleTest() {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/1.0/tasks"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("assignee", equalTo("me"))
                .withQueryParam("workspace", equalTo(testWorkspaceId))
                .withQueryParam("completed_since", equalTo("now"))
                .withQueryParam("opt_fields", equalTo(encodedOptFields))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON_CONTENT_TYPE)
                        .withBodyFile("my_tasks.json")));

        wireMockRule.stubFor(post(urlMatching("/api/1.0/tasks/[0-9]+/addProject")).willReturn(aResponse().withStatus(201)));

        wireMockRule.stubFor(get(urlMatching("/api/1.0/projects/[0-9]+"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("project.json")));

        wireMockRule.stubFor(post(urlMatching("/api/1.0/tasks/[0-9]+/stories"))
                .withRequestBody(containing("text=I+have+added+the+task+test+subtask+to+DevGraphicsRequests"))
                .willReturn(aResponse().withStatus(201)));

        wireMockRule.stubFor(put(urlMatching("/api/1.0/tasks/[0-9]+"))
                .withHeader("Content-Type", containing(APPLICATION_FORM_CONTENT_TYPE))
                .withRequestBody(matching("assignee=null"))
                .willReturn(aResponse().withStatus(201)));

        asanaService.addGraphicsProjectToGraphicsBotAssignedTasks("25587620489018");


        wireMockRule.verify(2, postRequestedFor(urlMatching("/api/1.0/tasks/[0-9]+/addProject"))
                        .withHeader("Content-Type", containing(APPLICATION_FORM_CONTENT_TYPE))
                        .withRequestBody(matching("project=[0-9]+"))
        );

    }

}
