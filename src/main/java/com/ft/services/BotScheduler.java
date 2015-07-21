package com.ft.services;

import java.io.IOException;

import com.ft.backup.AsanaBackupService;
import com.ft.monitoring.AsanaChangesService;
import com.ft.monitoring.ProjectChange;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class BotScheduler {

    Logger logger = LoggerFactory.getLogger(BotScheduler.class);

    private static final int ONE_DAY = 24 * 60 * 60_000;
    private static final int HALF_HOUR = 30 * 60_000;
    private static final int FIVE_MINUTES = 5 * 60_000;

    @Autowired @Setter
    private AsanaService asanaService;
    @Autowired @Setter
    private AsanaBackupService asanaBackupService;
    @Autowired @Setter
    private SlackService slackService;
    @Autowired @Setter
    private AsanaChangesService asanaChangesService;

    @Scheduled(fixedRate = 20000)
    public void graphicsBot() {
        asanaService.addGraphicsProjectToGraphicsBotAssignedTasks();
    }

    @Scheduled(fixedRate = 20000)
    public void picturesBot(){
        asanaService.addPicturesProjectToPicturesBotAssignedTasks();
    }

    @Scheduled(fixedRate = 20000)
    public void socialBot(){
        asanaService.addSocialProjectToSocialBotAssignedTasks();
    }

    @Scheduled(fixedRate = 20000)
    public void interactivesBot(){
        asanaService.addInteractivesProjectToInteractivesBotAssignedTasks();
    }

    @Scheduled(fixedRate = 20000)
    public void videoBot(){
        asanaService.addVideoProjectToVideoBotAssignedTasks();
    }


    @Scheduled(fixedRate = HALF_HOUR)
    public void backupAllProjects(){
        try {
            asanaBackupService.backupAllProjects();
        } catch (IOException e) {
            logger.error("error during projects backup", e);
        }
    }

    @Scheduled(fixedRate = ONE_DAY)
    public void removeOldBackupFiles(){
        try {
            asanaBackupService.removeOldBackupFiles();
        } catch (IOException e) {
            logger.error("error during deleting old backup files", e);
        }
    }

    @Scheduled(fixedRate = FIVE_MINUTES)
    public void checkForChanges() {
        List<ProjectChange> projectChanges = asanaChangesService.getChanges();
        try{
            slackService.notifyProjectChange(projectChanges);
        } catch (Exception ex) {
            logger.error("Could not post slack notification", ex);
        }
    }
}
