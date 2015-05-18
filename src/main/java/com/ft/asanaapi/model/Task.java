package com.ft.asanaapi.model;

/**
 * Basic info about a task.
 */
public class Task {
    public String id;
    public String name;
    public ParentTask parent;

    @Override
    public String toString() {
        return "id: " + id + " , name: " + name + "\n";
    }
}
