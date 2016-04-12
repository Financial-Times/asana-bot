package com.ft.backup.drive;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
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
    private final BatchRequest batch;

    public FileRemover(@NotNull Drive drive) {
        this.drive = drive;
        batch = drive.batch();
    }

    public void removeFilesOlderThan(File root, LocalDateTime dateTimeFrom) throws IOException {
        String formattedDateTimeFrom = dateTimeFrom.format(DateTimeFormatter.ISO_DATE_TIME);
        String query = buildQuery(root.getId(), formattedDateTimeFrom);
        FileList result = drive.files()
                .list()
                .setQ(query)
                .setPageSize(200)
                .execute();

        if (result.getFiles().size() > 0) {
            result.getFiles().parallelStream().forEach(this::queueDeleteFile);
            batch.execute();
        }
    }

    private String buildQuery(String folderId, String formattedDateTimeFrom) {
        return "'"+folderId+"' in parents and mimeType != '" + FOLDER_MIME_TYPE +
                "' and modifiedTime <= '" + formattedDateTimeFrom+"'";
    }

    private void queueDeleteFile(File file)  {
        try {
            drive.files().delete(file.getId()).queue(batch, callback);
        } catch (IOException e) {
            logger.error("Could not delete file" + file.getId(), e);
        }
    }

    private static final JsonBatchCallback<Void> callback = new JsonBatchCallback<Void>() {
        @Override
        public void onSuccess(Void aVoid, HttpHeaders responseHeaders) throws IOException {
        }

        @Override
        public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
            logger.warn("Could not delete file");
            logger.warn(e.getMessage());
        }

    };
}
