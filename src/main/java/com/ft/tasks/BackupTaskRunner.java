package com.ft.tasks;

import com.ft.backup.AsanaBackupService;
import com.ft.config.TaskBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BackupTaskRunner implements TaskRunner {
    private static Logger logger = LoggerFactory.getLogger(BackupTaskRunner.class);

    private final AsanaBackupService asanaBackupService;

    @Autowired
    BackupTaskRunner(AsanaBackupService asanaBackupService) {
        this.asanaBackupService = asanaBackupService;
    }

    @Override
    public void run(TaskBot taskbot) {
        try {
            asanaBackupService.backupAllProjects();
        } catch (IOException e) {
            logger.error("error during projects backup", e);
        }
    }
}
