package com.ft.backup.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileRemover {

    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
    private static final Logger logger = LoggerFactory.getLogger(FileRemover.class);

    private final Drive drive;

    public FileRemover(@NotNull Drive drive) {
        this.drive = drive;
    }

    public void removeFilesOlderThan(LocalDateTime dateTimeFrom) throws IOException {
        String formattedDateTimeFrom = dateTimeFrom.format(DateTimeFormatter.ISO_DATE_TIME);
        String query = "mimeType != '" + FOLDER_MIME_TYPE + "' and modifiedDate <= '" + formattedDateTimeFrom+"'";
        FileList result = drive.files().list()
                .setQ(query)
                .setMaxResults(200)
                .execute();

        result.getItems().parallelStream().forEach(this::deleteFile);
    }

    private void deleteFile(File file)  {
        try {
            drive.files().delete(file.getId()).execute();
        } catch (IOException e) {
            logger.error("Could not delete file" + file.getTitle(), e);
        }
    }
}
