package com.ft.asanaapi;

import com.asana.Client;
import com.asana.models.Project;
import com.asana.models.Tag;
import com.asana.models.Task;
import com.asana.models.Workspace;
import com.ft.asanaapi.model.ReportTasks;
import com.ft.report.model.ReportTask;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AsanaClientWrapper {
    private static final String TASK_FIELDS = "id,name,projects,parent.id,parent.name,parent.projects.team.name,projects.team.name,due_on,due_at";
    private static final String REPORT_TASK_FIELDS = "name,tags.name,due_on,notes,completed,subtasks.name,subtasks.completed";
    private static final String PROJECT_FIELDS = "this";

    private final Client client;
    private final ReportTasks extendedTasks;
    private final String workspaceId; //TODO refactor all methods not to use workspaceId

    public AsanaClientWrapper(Client client, String workspaceId) {
        this.client = client;
        this.extendedTasks = new ReportTasks(client);
        this.workspaceId = workspaceId;

    }

    public List<Task> getTasks(String workspaceId) throws IOException {
        return client.tasks.findAll()
                .query("assignee", "me")
                .query("workspace", workspaceId)
                .query("completed_since", "now")
                .query("opt_fields", TASK_FIELDS)
                .execute();
    }

    public List<Task> getTasksByProject(String projectId) throws IOException {
        return client.tasks.findByProject(projectId)
                .query("completed_since", "now")
                .query("opt_fields", TASK_FIELDS)
                .execute();
    }

    public Project getProject(String projectId) throws IOException {
        return client.projects.findById(projectId).execute();
    }

    public Task addTaskToProject(Task task, Project project) throws IOException {
        return client.tasks.addProject(task.id).data("project", project.id).execute();
    }

    public Task getTask(String taskId) throws IOException {
        return client.tasks.findById(taskId).query("opt_fields", TASK_FIELDS).execute();
    }

    public Task tagTask(Task task, Tag tag) throws IOException {
        return client.tasks.addTag(task.id).data("tag", tag.id).execute();
    }

    public Task unassignTask(Task task) throws IOException {
        return client.tasks.update(task.id).data("assignee", "null").execute();
    }

    public Optional<Tag> findTagsByWorkspace(String workspaceId) throws IOException {
        List<Workspace> tags = client.workspaces.typeahead(workspaceId)
                .query("query", "Big")
                .query("type", "tag")
                .execute();
        return tags.stream().map(this::toTag).findFirst();
    }
    public void updateTask(Task task, Map<String, Object> data) throws IOException {
        client.tasks.update(task.id).data(data).execute();
    }

    private Tag toTag(Workspace workspace) {
        Tag tag = new Tag();
        tag.id = workspace.id;
        tag.name = workspace.name;
        return tag;
    }

    public Tag createTag(String workspaceId, String name) throws IOException {
        return client.tags.createInWorkspace(workspaceId).data("name", name).execute();
    }

    public Task commentTask(Task task, String comment) throws IOException {
        return client.tasks.addComment(task.id).data("text", comment).execute();
    }

    public List<Project> getAllProjects(String workspaceId) throws IOException {
        return client.projects.findByWorkspace(workspaceId)
                .query("opt_expand", PROJECT_FIELDS).execute();
    }

    public Workspace getWorkspace(String workspace) throws IOException  {
        return client.workspaces.findById(workspace).execute();
    }

    public List<ReportTask> getReportTasks(String projectId) throws IOException  {
        return extendedTasks.findByProject(projectId)
                .query("workspace", workspaceId)
                .query("completed_since", "now")
                .query("opt_fields", REPORT_TASK_FIELDS)
                .execute();
    }
}
