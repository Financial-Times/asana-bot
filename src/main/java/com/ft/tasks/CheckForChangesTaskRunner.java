package com.ft.tasks;

import com.ft.config.TaskBot;
import com.ft.monitoring.AsanaChangesService;
import com.ft.monitoring.ProjectChange;
import com.ft.services.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckForChangesTaskRunner implements TaskRunner {
    private static Logger logger = LoggerFactory.getLogger(CheckForChangesTaskRunner.class);
    private AsanaChangesService asanaChangesService;
    private SlackService slackService;

    @Autowired
    public CheckForChangesTaskRunner(AsanaChangesService asanaChangesService, SlackService slackService) {
        this.asanaChangesService = asanaChangesService;
        this.slackService = slackService;
    }

    @Override
    public void run(TaskBot taskbot) {
        List<ProjectChange> projectChanges = asanaChangesService.getChanges();
        if (projectChanges == null || projectChanges.isEmpty()) {
            return;
        }
        tryToNotifyProjectChanges(projectChanges);
    }

    private void tryToNotifyProjectChanges(List<ProjectChange> projectChanges) {
        try{
            slackService.notifyProjectChange(projectChanges);
        } catch (Exception ex) {
            logger.error("Could not post slack notification", ex);
        }
    }
}
