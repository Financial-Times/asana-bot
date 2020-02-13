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
    private static final String TASK_FIELDS = "gid,name,projects,parent.gid,parent.name,parent.projects.team.name,projects.team.name,due_on,due_at";
    private static final String REPORT_TASK_FIELDS = "name,tags.name,due_on,notes,completed,subtasks.name,subtasks.completed,due_at,custom_fields";
    private static final String BACKUP_TASK_FIELDS = "gid,name,created_at,modified_at,completed,completed_at,assignee.name,due_on,tags.name,notes,projects.name,parent.name";
    private static final String PROJECT_FIELDS = "this";
    private static final String ENABLE_HEADERS = "string_ids,new_sections";

    private final Client client;
    private final ReportTasks extendedTasks;
    private final UserTeams userTeams;
    private final BackupTasks backupTasks;
    private final String workspaceId;

    public AsanaClientWrapper(Client client, String workspaceId) {
        this.client = client;
        this.client.headers.put("asana-enable", ENABLE_HEADERS);
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
        return client.tasks.addProject(task.gid).data("project", project.gid).execute();
    }

    public Task getTask(String taskId) throws IOException {
        return client.tasks.findById(taskId).query("opt_fields", TASK_FIELDS).execute();
    }

    public Task tagTask(Task task, Tag tag) throws IOException {
        return client.tasks.addTag(task.gid).data("tag", tag.gid).execute();
    }

    public Task unassignTask(Task task) throws IOException {
        return client.tasks.update(task.gid).data("assignee", "null").execute();
    }

    public Optional<Tag> findTagsByWorkspace(String tagName) throws IOException {
        List<Tag> tags = client.workspaces.tagTypeahead(workspaceId)
                .query("query", tagName)
                .query("type", "tag")
                .execute();
        return tags.stream().findFirst();
    }

    public void updateTask(Task task, Map<String, Object> data) throws IOException {
        client.tasks.update(task.gid).data(data).execute();
    }

    public Tag createTag(String name) throws IOException {
        return client.tags.createInWorkspace(workspaceId).data("name", name).execute();
    }

    public Task commentTask(Task task, String comment) throws IOException {
        return client.tasks.addComment(task.gid).data("text", comment).execute();
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
        List<User> users = client.workspaces.userTypeahead(String.valueOf(workspaceId)).query("query", email)
                .execute();
        return users.stream().findFirst();
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
