package com.ft.backup;

import com.asana.models.Project;
import com.ft.asanaapi.AsanaClientWrapper;
import com.ft.backup.csv.CsvTemplate;
import com.ft.backup.drive.GoogleDriveService;
import com.ft.backup.model.BackupTask;
import com.google.api.services.drive.model.File;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AsanaBackupService {

    private static final Logger logger = LoggerFactory.getLogger(AsanaBackupService.class);

    @Autowired private AsanaClientWrapper defaultAsanaClientWrapper;
    @Autowired private CsvTemplate csvTemplate;
    @Autowired private GoogleDriveService googleDriveService;

    @Setter private Clock clock = Clock.systemUTC();

    @Async
    public void backupAllProjects() throws IOException {
        File folder = googleDriveService.findOrCreateRootFolder();
        List<Project> projectsToBackup = defaultAsanaClientWrapper.findProjectsByWorkspace();
        projectsToBackup.stream().forEach(project -> this.backupProject(project, folder));
    }

    private void backupProject(Project project, File folder) {
        try {
            List<BackupTask> tasks = defaultAsanaClientWrapper.findAllTasksByProject(project.gid);
            String csv = toCSV(tasks);
            googleDriveService.uploadProjectFile(project, folder, csv);
        } catch (IOException e) {
            logger.error("Failed to backup file for project: " + project.name, e);
        }
    }

    private String toCSV(List<BackupTask> tasks) throws IOException {

        Writer writer = new StringWriter();

        try(ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE)) {
            final String[] header = csvTemplate.getHeaders();
            csvBeanWriter.writeHeader(header);

            final CellProcessor[] processors = csvTemplate.getProcessors();
            for(final BackupTask task: tasks) {
                csvBeanWriter.write(task, header, processors);
            }
        }
        return writer.toString();
    }

    @Async
    public void removeOldBackupFiles() throws IOException{
        LocalDateTime weekAgo = LocalDateTime.now(clock).minusDays(7);
        googleDriveService.removeFilesOlderThan(weekAgo);
    }
}
