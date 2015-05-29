package com.ft.asanaapi.model;

import lombok.Data;

import java.util.List;

/**
 * Basic info about a task.
 */
@Data
public class Task {
    private String id;
    private String name;
    private Task parent;
    private List<ProjectInfo> projects;

    public boolean isSubTask() {
        return parent != null;
    }

    @Override
    public String toString() {
        return "id: " + id + " , name: " + name + "\n";
    }
}
