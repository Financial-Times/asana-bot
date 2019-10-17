package com.ft.asanaapi;

import com.asana.Client;
import com.asana.models.*;
import com.ft.asanaapi.model.BackupTasks;
import com.ft.asanaapi.model.ReportTasks;
import com.ft.asanaapi.model.UserTeams;
import com.ft.backup.model.BackupTask;
import com.ft.report.model.ReportTask;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AsanaClientWrapper {
    private static final String TASK_FIELDS = "id,name,projects,parent.id,parent.name,parent.projects.team.name,projects.team.name,due_on,due_at";
    private static final String REPORT_TASK_FIELDS = "name,tags.name,due_on,notes,completed,subtasks.name,subtasks.completed,due_at,custom_fields";
    private static final String BACKUP_TASK_FIELDS = "id,name,created_at,modified_at,completed,completed_at,assignee.name,due_on,tags.name,notes,projects.name,parent.name";
    private static final String PROJECT_FIELDS = "this";
    private static final String DISABLE_HEADERS = "string_ids";

    private final Client client;
    private final ReportTasks extendedTasks;
    private final UserTeams userTeams;
    private final BackupTasks backupTasks;
    private final String workspaceId;

    public AsanaClientWrapper(Client client, String workspaceId) {
        this.client = client;
        this.client.headers.put("asana-disable", DISABLE_HEADERS); // this is a short term fix for asana deprecation of integer id as per https://asana.com/developers/news/feed
        this.extendedTasks = new ReportTasks(client);
        this.userTeams = new UserTeams(client);
        this.backupTasks = new BackupTasks(client);
        this.workspaceId = workspaceId;

    }

    public List<Task> getTasks() throws IOException {
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
                .query("limit", 100)
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

    public Optional<Tag> findTagsByWorkspace(String tagName) throws IOException {
        List<Workspace> tags = client.workspaces.typeahead(workspaceId)
                .query("query", tagName)
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

    public Tag createTag(String name) throws IOException {
        return client.tags.createInWorkspace(workspaceId).data("name", name).execute();
    }

    public Task commentTask(Task task, String comment) throws IOException {
        return client.tasks.addComment(task.id).data("text", comment).execute();
    }

    public List<Project> getAllProjects() throws IOException {
        return client.projects.findByWorkspace(workspaceId)
                .query("opt_expand", PROJECT_FIELDS).execute();
    }

    public Workspace getWorkspace() throws IOException  {
        return client.workspaces.findById(workspaceId).execute();
    }

    public List<ReportTask> getReportTasks(String projectId) throws IOException  {
        return extendedTasks.findByProject(projectId)
                .query("completed_since", "now")
                .query("opt_fields", REPORT_TASK_FIELDS)
                .execute();
    }

    public List<Team> getUserTeams(String userId) throws IOException  {
        return userTeams.findByUser(userId)
                .query("organization", workspaceId)
                .execute();
    }

    public Optional<User> findUsersByWorkspace(String email) throws IOException {
        List<Workspace> users = client.workspaces.typeahead(workspaceId)
                .query("query", email)
                .query("type", "user")
                .execute();
        return users.stream().map(this::toUser).findFirst();
    }

    private User toUser(Workspace workspace) {
        User user = new User();
        user.id = workspace.id;
        user.name = workspace.name;
        return user;
    }

    public List<Project> findProjectsByWorkspace() throws IOException {
        return client.projects.findByWorkspace(workspaceId)
                .query("opt_expand", "this")
                .execute();
    }

    public List<BackupTask> findAllTasksByProject(String projectId) {
        List<BackupTask> tasks = new LinkedList<>();
        Iterable<BackupTask> tasksIterable = backupTasks.findByProject(projectId)
                .query("opt_fields", BACKUP_TASK_FIELDS)
                .option("page_size", 100);
        //Redundant for loop is for Asana PageIterator so that it can page results
        if(tasksIterable != null) {
            for (BackupTask task : tasksIterable) {
                tasks.add(task);
            }
        }
        return tasks;
    }
}
