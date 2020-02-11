package com.ft.backup.model;

import com.asana.models.Project;
import com.asana.models.Tag;
import com.asana.models.User;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class BackupTask {

    private String gid;
    private String name;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("modified_at")
    private String modifiedAt;
    private Boolean completed = Boolean.FALSE;
    @SerializedName("completed_at")
    private String completedAt;
    private User assignee;
    @SerializedName("due_on")
    private String dueOn;
    private List<Tag> tags;
    private String notes;

    private BackupTask parent;
    private List<Project> projects;
}
