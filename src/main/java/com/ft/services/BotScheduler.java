package com.ft.services;

import com.ft.backup.AsanaBackupService;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BotScheduler {

    Logger logger = LoggerFactory.getLogger(BotScheduler.class);

    private static final int ONE_DAY = 24 * 60 * 60_000;
    private static final int HALF_HOUR = 30 * 60_000;

    @Autowired @Setter
    private AsanaService asanaService;
    @Autowired @Setter
    private AsanaBackupService asanaBackupService;

    @Scheduled(fixedRate = 20000)
    public void graphicsBot() {
        asanaService.addGraphicsProjectToGraphicsBotAssignedTasks();
    }

    @Scheduled(fixedRate = 20000)
    public void picturesBot(){
        asanaService.addPicturesProjectToPicturesBotAssignedTasks();
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
}
