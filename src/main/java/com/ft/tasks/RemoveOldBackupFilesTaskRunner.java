package com.ft.tasks;

import com.ft.backup.AsanaBackupService;
import com.ft.config.TaskBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RemoveOldBackupFilesTaskRunner implements TaskRunner {
    private static Logger logger = LoggerFactory.getLogger(BackupTaskRunner.class);

    private final AsanaBackupService asanaBackupService;

    @Autowired
    RemoveOldBackupFilesTaskRunner(AsanaBackupService asanaBackupService) {
        this.asanaBackupService = asanaBackupService;
    }

    @Override
    public void run(TaskBot taskbot) {
        try {
            asanaBackupService.removeOldBackupFiles();
        } catch (IOException e) {
            logger.error("error during removing old backup files", e);
        }
    }
}
