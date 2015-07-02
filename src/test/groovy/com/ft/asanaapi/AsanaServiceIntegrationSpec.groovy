package com.ft.asanaapi

import com.ft.services.AsanaService
import com.ft.test.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired

import static com.github.tomakehurst.wiremock.client.WireMock.*

public class AsanaServiceIntegrationSpec extends IntegrationSpec {

    private static final String encodedOptFields = "id%2Cname%2Cparent.id%2Cparent.name%2Cparent.projects.team.name%2Cprojects.team.name"
    private static final String decodedOptFields = "id,name,parent.id,parent.name,parent.projects.team.name,projects.team.name"
    private static final String APPLICATION_FORM_CONTENT_TYPE = "application/x-www-form-urlencoded"


    @Autowired
    AsanaService asanaService

    public void "add graphics project to graphics bot assignedTasks"() {
        given:
            String subTaskThatHasSubTaskId = '34761621630973'
            String subTaskEncoded = 'test+subtask'
            String subSubTaskEncoded = 'test+sub+subtask'
        and:
            stubGetTasks()
            stubGetProjects()
            stubGetWorkspaceTags()
            stubPostAddProject()
            stubPostStories(subTaskEncoded)
            stubPostStories(subSubTaskEncoded)
            stubGetTaskDetails(subTaskThatHasSubTaskId)
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
            verifyPostStories(subTaskEncoded)
            verifyPostStories(subSubTaskEncoded)
            verifyGetTaskDetails(subTaskThatHasSubTaskId)
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
                .withBodyFile("tasks/my_tasks.json")))
    }

    private stubGetProjects() {
        wireMockRule.stubFor(get(urlMatching("/api/1.0/projects/[0-9]+"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("tasks/project.json")))
    }

    private stubGetWorkspaceTags() {
        wireMockRule.stubFor(get(urlMatching("/api/1.0/workspaces/"+testWorkspaceId+"/typeahead\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("type", equalTo("tag"))
                .withQueryParam("query", matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("tasks/tags.json")))
    }

    private stubPostAddProject() {
        wireMockRule.stubFor(post(urlMatching("/api/1.0/tasks/[0-9]+/addProject")).willReturn(aResponse().withStatus(201)))
    }

    private stubPostStories(String taskName) {
        wireMockRule.stubFor(post(urlMatching("/api/1.0/tasks/[0-9]+/stories"))
                .withRequestBody(containing("text=I+have+added+the+task+${taskName}+to+DevGraphicsRequests"))
                .willReturn(aResponse().withStatus(201)))
    }

    private stubGetTaskDetails(String taskId) {
        wireMockRule.stubFor(get(urlMatching("/api/1.0/tasks/${taskId}.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("opt_fields", equalTo(encodedOptFields))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile('tasks/subtask-details.json')))
    }

    private stubPostTag() {
        wireMockRule.stubFor(post(urlMatching("/api/1.0/workspaces/"+testWorkspaceId+"/tags"))
                .withRequestBody(containing("name=MKT"))
                .willReturn(aResponse()
                .withStatus(201)
                .withBodyFile("tasks/create_tag.json")))
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
        wireMockRule.verify(5, postRequestedFor(urlMatching("/api/1.0/tasks/[0-9]+/addProject"))
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
        wireMockRule.verify(4, getRequestedFor(urlMatching("/api/1.0/workspaces/"+testWorkspaceId+"/typeahead\\?.*"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("type", equalTo("tag"))
                .withQueryParam("query", matching(".*")))
        return true
    }

    private boolean verifyPostStories(String taskName) {
        wireMockRule.verify(1, postRequestedFor(urlMatching("/api/1.0/tasks/[0-9]+/stories"))
                .withRequestBody(containing("text=I+have+added+the+task+${taskName}+to+DevGraphicsRequests"))
                )
        return true
    }

    private verifyGetTaskDetails(String taskId) {
        wireMockRule.verify(1, getRequestedFor(urlPathEqualTo("/api/1.0/tasks/${taskId}"))
                .withHeader("Authorization", containing(BASIC_AUTH_HEADER))
                .withQueryParam("opt_fields", equalTo(decodedOptFields))
        )
        return true
    }

    private boolean verifyPostTag() {
        wireMockRule.verify(3, postRequestedFor(urlMatching("/api/1.0/workspaces/"+testWorkspaceId+"/tags"))
                .withRequestBody(containing("name=MKT")))
        return true
    }

    private boolean verifyPostAddTag() {
        wireMockRule.verify(4, postRequestedFor(urlMatching("/api/1.0/tasks/[0-9]+/addTag"))
                .withHeader("Content-Type", containing(APPLICATION_FORM_CONTENT_TYPE))
                .withRequestBody(matching("tag=[0-9]+")))
        return true
    }

    private boolean verifyPutTasks() {
        wireMockRule.verify(5, putRequestedFor(urlMatching("/api/1.0/tasks/[0-9]+"))
                .withHeader("Content-Type", containing(APPLICATION_FORM_CONTENT_TYPE))
                .withRequestBody(matching("assignee=null")))
        return true
    }

}
