package com.ft.asanaapi.model;

import lombok.Data;

/**
 * Basic info about a task.
 */
@Data
public class Task {
    private String id;
    private String name;
    private ParentTask parent;

    public boolean isSubTask() {
        return parent != null;
    }

    @Override
    public String toString() {
        return "id: " + id + " , name: " + name + "\n";
    }
}
