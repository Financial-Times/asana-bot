package com.ft.asanaapi.model;

import java.util.List;

/**
 * A class to respresent the data wrapper object which contains a list of task info
 */
public class TasksData {
    public List<Task> data;
    @Override
    public String toString(){
        String intro = (data != null || data.size() == 0) ? data.size() + " tasks: \n" : "No Tasks";
        return  intro + data.parallelStream().map(data -> data.toString()).reduce("", (a,b) -> a + b );
    }
}
