package com.ft.monitoring;

import com.ft.asanaapi.model.ProjectInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString
@EqualsAndHashCode
public class ProjectChange {

    private final ProjectInfo project;
    private final List<Change> changes;

    public ProjectChange(ProjectInfo project) {
        this.project = project;
        this.changes = new ArrayList<>();
    }

    public boolean isProjectChanged() {
        return !changes.isEmpty();
    }

    public void build(ProjectInfo referenceProject) {
        checkName(referenceProject);
        checkArchived(referenceProject);
        checkTeam(referenceProject);
    }

    private void checkName(ProjectInfo referenceProject) {
        String oldName = referenceProject.getName();
        String newName = project.getName();
        if (!newName.equals(oldName)) {
            addChangeType(oldName, newName, ChangeType.NAME);
        }
    }

    private void checkArchived(ProjectInfo referenceProject) {
        Boolean oldArchived = referenceProject.getArchived();
        Boolean newArchived = project.getArchived();
        if (!newArchived.equals(oldArchived)) {
            addChangeType(oldArchived.toString(), newArchived.toString(), ChangeType.ARCHIVED);
        }
    }

    private void checkTeam(ProjectInfo referenceProject) {
        String oldTeam = referenceProject.getTeam().getName();
        String newTeam = project.getTeam().getName();
        if (!newTeam.equals(oldTeam)) {
            addChangeType(oldTeam, newTeam, ChangeType.TEAM);
        }
    }

    private void addChangeType(String oldValue, String newValue, ChangeType changeType) {
        Change change = new Change(oldValue, newValue, changeType);
        changes.add(change);
    }
}
