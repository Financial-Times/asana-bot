package com.ft.asanaapi.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectInfo extends AsanaEntity {
    private String notes;
    private Team team;
    private Boolean archived = Boolean.FALSE;

    public boolean isAssignedToTeam() {
        return team != null;
    }
}
