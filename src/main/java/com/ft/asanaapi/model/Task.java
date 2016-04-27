package com.ft.asanaapi.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Basic info about a task.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends AsanaEntity {
    private Task parent;
    private List<ProjectInfo> projects;

    @Override
    public String toString() {
        return "id: " + getId() + " , name: " + getName() + "\n";
    }
}
