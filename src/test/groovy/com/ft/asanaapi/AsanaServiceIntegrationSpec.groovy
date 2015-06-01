package com.ft.asanaapi

import com.ft.AsanaBot
import com.ft.services.AsanaService
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig


@IntegrationTest
@ContextConfiguration(classes = AsanaBot.class, loader = SpringApplicationContextLoader.class)
@ActiveProfiles("test")
public class AsanaServiceIntegrationSpec extends Specification {

    private static final String testWorkspaceId = "324300775153"
    private static final String encodedOptFields = "id%2Cname%2Cparent.id%2Cparent.name%2Cparent.projects.team.name%2Cprojects.team.name"
    private static final String decodedOptFields = "id,name,parent.id,parent.name,parent.projects.team.name,projects.team.name"

    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json"
    private static final String APPLICATION_FORM_CONTENT_TYPE = "application/x-www-form-urlencoded"
    private static final String BASIC_AUTH_HEADER = "Basic "

    @Autowired
    AsanaService asanaService

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8888))

    public void "add graphics project to graphics bot assignedTasks"() {
        given:
            stubGetTasks()
            stubGetProjects()
            stubGetWorkspaceTags()
            stubPostAddProject()
            stubPostStories()
            stubPostTag()
            stubPostAddTag()
            stubPutTasks()

        when:
            asanaService.addGraphicsProjectToGraphicsBotAssignedTasks()

        then:
            verifyGetTasks()
            verifyGetProjects()
            verifyWorkspaceTags()
            verifyPostAddProject()
            verifyPostStories()
            verifyPostTag()
            verifyPostAddTag()
            verifyPutTasks()
    }

    private stubGetTasks() {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/1.0/tasks"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("assignee", equalTo("me"))
                .withQueryParam("workspace", equalTo(testWorkspaceId))
                .withQueryParam("completed_since", equalTo("now"))
                .withQueryParam("opt_fields", equalTo(encodedOptFields))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", APPLICATION_JSON_CONTENT_TYPE)
                .withBodyFile("my_tasks.json")))
    }

    private stubGetProjects() {
        wireMockRule.stubFor(get(urlMatching("/api/1.0/projects/[0-9]+"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("project.json")))
    }

    private stubGetWorkspaceTags() {
        wireMockRule.stubFor(get(urlMatching("/api/1.0/workspaces/"+testWorkspaceId+"/typeahead\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("type", equalTo("tag"))
                .withQueryParam("query", matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("tags.json")))
    }

    private stubPostAddProject() {
        wireMockRule.stubFor(post(urlMatching("/api/1.0/tasks/[0-9]+/addProject")).willReturn(aResponse().withStatus(201)))
    }

    private stubPostStories() {
        wireMockRule.stubFor(post(urlMatching("/api/1.0/tasks/[0-9]+/stories"))
                .withRequestBody(containing("text=I+have+added+the+task+test+subtask+to+DevGraphicsRequests"))
                .willReturn(aResponse().withStatus(201)))
    }

    private stubPostTag() {
        wireMockRule.stubFor(post(urlMatching("/api/1.0/workspaces/"+testWorkspaceId+"/tags"))
                .withRequestBody(containing("name=Market"))
                .willReturn(aResponse().withStatus(201).withBodyFile("create_tag.json")))
    }

    private stubPostAddTag() {
        wireMockRule.stubFor(post(urlMatching("/api/1.0/tasks/[0-9]+/addTag"))
                .withHeader("Content-Type", containing(APPLICATION_FORM_CONTENT_TYPE))
                .withRequestBody(matching("tag=[0-9]+"))
                .willReturn(aResponse().withStatus(201)))
    }

    private stubPutTasks() {
        wireMockRule.stubFor(put(urlMatching("/api/1.0/tasks/[0-9]+"))
                .withHeader("Content-Type", containing(APPLICATION_FORM_CONTENT_TYPE))
                .withRequestBody(matching("assignee=null"))
                .willReturn(aResponse().withStatus(201)))
    }

    private boolean verifyGetTasks() {
        wireMockRule.verify(1, getRequestedFor(urlMatching("/api/1.0/tasks\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("assignee", equalTo("me"))
                .withQueryParam("workspace", equalTo(testWorkspaceId))
                .withQueryParam("completed_since", equalTo("now"))
                .withQueryParam("opt_fields", matching(decodedOptFields)))
        return true
    }

    private boolean verifyPostAddProject() {
        wireMockRule.verify(4, postRequestedFor(urlMatching("/api/1.0/tasks/[0-9]+/addProject"))
                .withHeader("Content-Type", containing(APPLICATION_FORM_CONTENT_TYPE))
                .withRequestBody(matching("project=[0-9]+")))
        return true
    }

    private boolean verifyGetProjects() {
        wireMockRule.verify(1, getRequestedFor(urlMatching("/api/1.0/projects/[0-9]+"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER)))
        return true
    }

    private boolean verifyWorkspaceTags() {
        wireMockRule.verify(3, getRequestedFor(urlMatching("/api/1.0/workspaces/"+testWorkspaceId+"/typeahead\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("type", equalTo("tag"))
                .withQueryParam("query", matching(".*")))
        return true
    }

    private boolean verifyPostStories() {
        wireMockRule.verify(1, postRequestedFor(urlMatching("/api/1.0/tasks/[0-9]+/stories"))
                .withRequestBody(containing("text=I+have+added+the+task+test+subtask+to+DevGraphicsRequests")))
        return true
    }

    private boolean verifyPostTag() {
        wireMockRule.verify(2, postRequestedFor(urlMatching("/api/1.0/workspaces/"+testWorkspaceId+"/tags"))
                .withRequestBody(containing("name=Market")))
        return true
    }

    private boolean verifyPostAddTag() {
        wireMockRule.verify(3, postRequestedFor(urlMatching("/api/1.0/tasks/[0-9]+/addTag"))
                .withHeader("Content-Type", containing(APPLICATION_FORM_CONTENT_TYPE))
                .withRequestBody(matching("tag=[0-9]+")))
        return true
    }

    private boolean verifyPutTasks() {
        wireMockRule.verify(4, putRequestedFor(urlMatching("/api/1.0/tasks/[0-9]+"))
                .withHeader("Content-Type", containing(APPLICATION_FORM_CONTENT_TYPE))
                .withRequestBody(matching("assignee=null")))
        return true
    }

}
