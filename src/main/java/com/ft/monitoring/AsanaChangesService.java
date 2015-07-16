package com.ft.monitoring;

import com.ft.asanaapi.AsanaClient;
import com.ft.asanaapi.model.ProjectInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AsanaChangesService {
    @Autowired private AsanaClient reportAsanaClient;

    private List<ProjectInfo> previousProjects;
    private List<ProjectInfo> currentProjects;

    public List<ProjectChange> getChanges() {
        if (previousProjects == null) {
            previousProjects = reportAsanaClient.getAllProjects();
            return null;
        }

        currentProjects = reportAsanaClient.getAllProjects();
        List<ProjectChange> projectChanges = new ArrayList<>();

        previousProjects.stream().forEach( previousProject -> {
            Optional<ProjectInfo> currentProject = findMatchingProject(previousProject);
            if (currentProject.isPresent()) {
                checkForChanges(projectChanges, previousProject, currentProject.get());
            }
        });
        return projectChanges;
    }

    private Optional<ProjectInfo> findMatchingProject(ProjectInfo previousProject) {
        return currentProjects.stream()
                .filter(matchingProject -> matchingProject.getId().equals(previousProject.getId()))
                .findFirst();
    }

    private void checkForChanges(List<ProjectChange> projectChanges, ProjectInfo previousProject, ProjectInfo currentProject) {
        ProjectChange projectChange = new ProjectChange(currentProject);
        projectChange.build(previousProject);

        if (!projectChange.getChanges().isEmpty()) {
            projectChanges.add(projectChange);
        }
    }

}
