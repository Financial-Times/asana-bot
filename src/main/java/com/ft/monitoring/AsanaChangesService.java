package com.ft.monitoring;

import com.ft.asanaapi.AsanaClient;
import com.ft.asanaapi.model.ProjectInfo;
import com.ft.asanaapi.model.Team;
import com.ft.report.model.Desk;
import com.ft.report.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AsanaChangesService {
    @Autowired private AsanaClient reportAsanaClient;
    @Autowired private DeskConfig deskConfig;

    public List<ProjectChange> getChanges() {
        List<ProjectChange> projectChanges = new ArrayList<>();
        List<ProjectInfo> currentProjects = reportAsanaClient.getAllProjects();
        deskConfig.getDesks().forEach((teamName, desk) -> {
            desk.getProjects().forEach((project) -> {
                Optional<ProjectInfo> currentProject = findMatchingProject(currentProjects, project.getId().toString());
                ProjectInfo projectInfo = currentProject.isPresent() ? currentProject.get() : null;
                ProjectInfo previousProject = createReferenceProject(teamName, project);
                checkForChanges(projectChanges, previousProject, projectInfo);
            });
        });
        return projectChanges;
    }

    private Optional<ProjectInfo> findMatchingProject(List<ProjectInfo> currentProjects, String projectId) {

        return currentProjects.stream()
                .filter(matchingProject -> matchingProject.getId().equals(projectId))
                .findFirst();
    }

    private ProjectInfo createReferenceProject(String teamName, Project project) {
        ProjectInfo previousProject = new ProjectInfo();
        previousProject.setId(project.getId().toString());
        previousProject.setArchived(Boolean.FALSE);
        previousProject.setName(project.getName());
        Team previousTeam = new Team();
        previousTeam.setName(teamName);
        previousProject.setTeam(previousTeam);
        return previousProject;
    }

    private void checkForChanges(List<ProjectChange> projectChanges, ProjectInfo previousProject, ProjectInfo currentProject) {
        ProjectChange projectChange = new ProjectChange(currentProject, previousProject);

        if (!projectChange.getChanges().isEmpty()) {
            projectChanges.add(projectChange);
        }
    }

}
