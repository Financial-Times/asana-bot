package com.ft.asanaapi.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Basic info about a task.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends AsanaEntity {
    private Task parent;
    private List<ProjectInfo> projects;
    private List<Tag> tags;

    public boolean isSubTask() {
        return parent != null;
    }

    @Override
    public String toString() {
        return "id: " + getId() + " , name: " + getName() + "\n";
    }

    public String toLongString() {
        return "id: " + getId() + " , name: " + getName() + " , tags: " + getTags() + "\n";
    }
}
