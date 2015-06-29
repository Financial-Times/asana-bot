package com.ft.backup.model;

import com.ft.asanaapi.model.Tag;
import com.ft.asanaapi.model.Task;
import com.ft.asanaapi.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BackupTask extends Task {

    private String created_at;
    private String modified_at;
    private Boolean completed = Boolean.FALSE;
    private String completed_at;
    private User assignee;
    private String due_date;
    private List<Tag> tags;
    private String notes;
}
