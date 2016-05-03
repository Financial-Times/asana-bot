package com.ft.monitoring;

import com.asana.models.Project;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString
@EqualsAndHashCode(of = "changes")
public class ProjectChange {

    private final Project project;
    private Project referenceProject;
    private final List<Change> changes;

    public ProjectChange(Project project, Project referenceProject) {
        this.project = project;
        this.referenceProject = referenceProject;
        this.changes = new ArrayList<>();
        build(this.referenceProject);
    }

    public boolean isProjectChanged() {
        return !changes.isEmpty();
    }

    private void build(Project referenceProject) {
        if (project == null) {
            addChangeType(referenceProject.name, null, ChangeType.NOT_FOUND);
            return;
        }
        checkName(referenceProject);
        checkArchived(referenceProject);
        checkTeam(referenceProject);
    }

    private void checkName(Project referenceProject) {
        String oldName = referenceProject.name;
        String newName = project.name;
        if (!newName.equals(oldName)) {
            addChangeType(oldName, newName, ChangeType.NAME);
        }
    }

    private void checkArchived(Project referenceProject) {
        Boolean oldArchived = referenceProject.isArchived;
        Boolean newArchived = project.isArchived;
        if (!newArchived.equals(oldArchived)) {
            addChangeType(oldArchived.toString(), newArchived.toString(), ChangeType.ARCHIVED);
        }
    }

    private void checkTeam(Project referenceProject) {
        String oldTeam = referenceProject.team.name;
        String newTeam = project.team.name;
        if (!newTeam.equals(oldTeam)) {
            addChangeType(oldTeam, newTeam, ChangeType.TEAM);
        }
    }

    private void addChangeType(String oldValue, String newValue, ChangeType changeType) {
        Change change = new Change(oldValue, newValue, changeType);
        changes.add(change);
    }
}
