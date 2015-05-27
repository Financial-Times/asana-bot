package com.ft.asanaapi.model;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * A class to respresent the data wrapper object which contains a list of task info
 */
@Data
public class TasksData {

    private List<Task> data;

    @Override
    public String toString(){
        if (data == null || data.size() == 0) {
            return "No tasks";
        }
        String intro = data.size() + " tasks: \n";
        return  intro + data.parallelStream().map(Task::toString).reduce("", (a, b) -> a + b);
    }
}
