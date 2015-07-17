package com.ft.monitoring;

import com.ft.asanaapi.AsanaClient;
import com.ft.asanaapi.model.ProjectInfo;
import com.ft.asanaapi.model.Team;
import com.ft.report.model.Desk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AsanaChangesService {
    @Autowired private AsanaClient reportAsanaClient;
    @Autowired private DeskConfig deskConfig;

    public List<ProjectChange> getChanges() {
        List<ProjectChange> projectChanges = new ArrayList<>();
        deskConfig.getDesks().forEach((teamName, desk) -> {
            Optional<ProjectInfo> currentProject = findMatchingProject(desk.getProjectId());
            if (currentProject.isPresent()) {
                ProjectInfo previousProject = createReferenceProject(teamName, desk);
                checkForChanges(projectChanges, previousProject, currentProject.get());
            }
        });
        return projectChanges;
    }

    private Optional<ProjectInfo> findMatchingProject(String projectId) {
        List<ProjectInfo> currentProjects = reportAsanaClient.getAllProjects();
        return currentProjects.stream()
                .filter(matchingProject -> matchingProject.getId().equals(projectId))
                .findFirst();
    }

    private ProjectInfo createReferenceProject(String teamName, Desk desk) {
        ProjectInfo previousProject = new ProjectInfo();
        previousProject.setId(desk.getProjectId());
        previousProject.setArchived(Boolean.FALSE);
        previousProject.setName(desk.getProjectName());
        Team previousTeam = new Team();
        previousTeam.setName(teamName);
        previousProject.setTeam(previousTeam);
        return previousProject;
    }

    private void checkForChanges(List<ProjectChange> projectChanges, ProjectInfo previousProject, ProjectInfo currentProject) {
        ProjectChange projectChange = new ProjectChange(currentProject);
        projectChange.build(previousProject);

        if (!projectChange.getChanges().isEmpty()) {
            projectChanges.add(projectChange);
        }
    }

}
