package com.ft.backup;

import com.ft.asanaapi.AsanaClient;
import com.ft.asanaapi.model.ProjectInfo;
import com.ft.backup.csv.CsvTemplate;
import com.ft.backup.drive.GoogleDriveService;
import com.ft.backup.model.BackupTask;
import com.google.api.services.drive.model.File;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired private AsanaClient reportAsanaClient;
    @Autowired private CsvTemplate csvTemplate;
    @Autowired private GoogleDriveService googleDriveService;

    @Setter private Clock clock = Clock.systemUTC();

    public void backupAllProjects() throws IOException {
        File folder = googleDriveService.findOrCreateRootFolder();
        List<ProjectInfo> projectsToBackup = reportAsanaClient.getAllProjects();
        projectsToBackup.stream().forEach(project -> this.backupProject(project, folder));
    }

    private void backupProject(ProjectInfo project, File folder) {
        List<BackupTask> tasks = reportAsanaClient.getAllTasksByProject(project);
        try {
            String csv = toCSV(tasks);
            googleDriveService.uploadProjectFile(project, folder, csv);
        } catch (IOException e) {
            logger.error("Failed to backup file for project: " + project.getName(), e);
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

    public void removeOldBackupFiles() throws IOException{
        LocalDateTime weekAgo = LocalDateTime.now(clock).minusDays(7);
        googleDriveService.removeFilesOlderThan(weekAgo);
    }
}
