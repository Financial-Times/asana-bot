package com.ft.monitoring;

import com.ft.asanaapi.AsanaClient;
import com.ft.asanaapi.model.ProjectInfo;
import com.ft.backup.model.ProjectsData;
import lombok.ToString;
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
            Optional<ProjectInfo> currentOptionalProject = currentProjects.stream()
                    .filter(matchingProject -> matchingProject.getId().equals(previousProject.getId()))
                    .findFirst();

            if (currentOptionalProject.isPresent()) {
                ProjectInfo currentProject = currentOptionalProject.get();
                ProjectChange projectChange = new ProjectChange();
                projectChange.setProject(currentProject);
                if (!currentProject.getName().equals(previousProject.getName())) {
                    projectChange.addChangeType(ChangeType.NAME);
                }
                if (!currentProject.isArchived() == previousProject.isArchived()) {
                    projectChange.addChangeType(ChangeType.ARCHIVED);
                }
                if (!currentProject.getTeam().equals(previousProject.getTeam())) {
                    projectChange.addChangeType(ChangeType.TEAM);
                }

                if (!projectChange.getChangeTypes().isEmpty()) {
                    projectChanges.add(projectChange);
                }
            }
        });
        return projectChanges;
    }

}
