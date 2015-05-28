package com.ft.asanaapi.model;

import lombok.Data;

@Data
public class ProjectInfo {
    private String id;
    private String name;
    private String notes;
    private Team team;

    public boolean isAssignedToTeam() {
        return team != null;
    }
}
