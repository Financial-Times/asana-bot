package com.ft.backup.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
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

    public void removeFilesOlderThan(File root, LocalDateTime dateTimeFrom) throws IOException {
        String formattedDateTimeFrom = dateTimeFrom.format(DateTimeFormatter.ISO_DATE_TIME);
        String query = buildQuery(formattedDateTimeFrom);
        ChildList result = drive.children().list(root.getId())
                .setQ(query)
                .setMaxResults(200)
                .execute();

        result.getItems().parallelStream().forEach(this::deleteFile);
    }

    private String buildQuery(String formattedDateTimeFrom) {
        return "mimeType != '" + FOLDER_MIME_TYPE + "' and modifiedDate <= '" + formattedDateTimeFrom+"'";
    }

    private void deleteFile(ChildReference file)  {
        try {
            drive.files().delete(file.getId()).execute();
        } catch (IOException e) {
            logger.error("Could not delete file" + file.getId(), e);
        }
    }
}
