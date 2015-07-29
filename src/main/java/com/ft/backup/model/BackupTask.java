package com.ft.backup.model;

import com.ft.asanaapi.model.Tag;
import com.ft.asanaapi.model.Task;
import com.ft.asanaapi.model.User;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BackupTask extends Task {

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
}
