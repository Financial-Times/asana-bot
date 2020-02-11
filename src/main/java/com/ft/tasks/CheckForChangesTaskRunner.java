package com.ft.tasks;

import com.asana.models.Project;
import com.asana.models.Team;
import com.ft.config.TaskBot;
import com.ft.monitoring.DeskConfig;
import com.ft.monitoring.ProjectChange;
import com.ft.report.model.Desk;
import com.ft.services.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Component
public class CheckForChangesTaskRunner implements TaskRunner {
    private static Logger logger = LoggerFactory.getLogger(CheckForChangesTaskRunner.class);
    private SlackService slackService;
    private DeskConfig deskConfig;

    @Autowired
    public CheckForChangesTaskRunner(SlackService slackService, DeskConfig deskConfig) {
        this.slackService = slackService;
        this.deskConfig = deskConfig;
    }

    @Override
    public void run(TaskBot taskbot) {
        try{
            List<ProjectChange> projectChanges = getChanges(taskbot);
            if (projectChanges == null || projectChanges.isEmpty()) {
                return;
            }
            tryToNotifyProjectChanges(projectChanges);
        } catch (IOException ex) {
            logger.error("Could not check for changes", ex);
        }

    }

    private void tryToNotifyProjectChanges(List<ProjectChange> projectChanges) {
        try{
            slackService.notifyProjectChange(projectChanges);
        } catch (Exception ex) {
            logger.error("Could not post slack notification", ex);
        }
    }

    private List<ProjectChange> getChanges(TaskBot taskBot) throws IOException {
        List<ProjectChange> projectChanges = new ArrayList<>();
        List<Project> currentProjects = taskBot.getClient().getAllProjects();
        nornaliseDesk().forEach((teamName, desk) -> desk.getProjects().forEach((projectSummary) -> {
            Optional<Project> currentProjectCandidate = findMatchingProject(currentProjects, projectSummary.getId().toString());
            Project currentProject = currentProjectCandidate.isPresent() ? currentProjectCandidate.get() : null;
            Project asanaProject = new Project();
            asanaProject.gid = projectSummary.getId().toString();
            asanaProject.name = projectSummary.getName();
            Project previousProject = createReferenceProject(teamName, asanaProject);
            checkForChanges(projectChanges, previousProject, currentProject);
        }));
        return projectChanges;
    }

    private Optional<Project> findMatchingProject(List<Project> currentProjects, String projectId) {
        return currentProjects.stream()
                .filter(matchingProject -> matchingProject.gid.equals(projectId))
                .findFirst();
    }

    private Project createReferenceProject(String teamName, Project project) {
        Project previousProject = new Project();
        previousProject.gid= project.gid;
        previousProject.isArchived = Boolean.FALSE;
        previousProject.name = project.name;
        Team previousTeam = new Team();
        previousTeam.name = teamName;
        previousProject.team = previousTeam;
        return previousProject;
    }

    private void checkForChanges(List<ProjectChange> projectChanges, Project previousProject, Project currentProject) {
        ProjectChange projectChange = new ProjectChange(currentProject, previousProject);

        if (!projectChange.getChanges().isEmpty()) {
            projectChanges.add(projectChange);
        }
    }

    private Map<String, Desk> nornaliseDesk() {
        Map<String, Desk> normalisedDesk = new HashMap<>();
        deskConfig.getDesks().forEach((teamName, desk) ->{
            List<com.ft.report.model.Project> normalisedProjects = new ArrayList<>();
            buildProjectFromList(desk, normalisedProjects);
            desk.setProjects(normalisedProjects);
            normalisedDesk.put(teamName, desk);
        });
        return normalisedDesk;
    }

    private void buildProjectFromList(Desk desk, List<com.ft.report.model.Project> normalisedProjects) {
        for (com.ft.report.model.Project project : desk.getProjects()) {
            if (project.getName().contains(",")){
               String[] projectNamesWithComma = StringUtils.commaDelimitedListToStringArray(project.getName());
                String[] projectIdsWithComma = StringUtils.commaDelimitedListToStringArray(project.getId());

                for (int i =0; i < projectNamesWithComma.length; i++){
                    com.ft.report.model.Project normalisedProject = new com.ft.report.model.Project(projectIdsWithComma[i], projectNamesWithComma[i], project.getPrimary());
                    normalisedProjects.add(normalisedProject);
                }
            } else {
                normalisedProjects.add(project);
            }
        }
    }
}
