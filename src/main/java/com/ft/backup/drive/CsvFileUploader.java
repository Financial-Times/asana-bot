package com.ft.backup.drive;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import javax.validation.constraints.NotNull;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class CsvFileUploader {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    private final Drive drive;
    private final String name;
    private final File parent;
    private final File fileToUpload;
    private final InputStreamContent mediaContent;

    public CsvFileUploader(@NotNull Drive drive, @NotNull String name, @NotNull File parent, @NotNull String content) throws IOException {
        this.drive = drive;
        this.name = name;
        this.parent = parent;
        this.fileToUpload = buildFile();
        this.mediaContent = buildMediaContent(content);
    }

    public File upload() throws IOException{
        Drive.Files.Create request = drive.files().create(fileToUpload, mediaContent);
        File newFile = request.execute();
        return newFile;
    }

    private File buildFile() throws IOException {
        return new File()
            .setDescription(name)
            .setMimeType("application/vnd.google-apps.spreadsheet")
            .setName(buildFileTitle(name))
            .setParents(Collections.singletonList(parent.getId()));
    }

    private String buildFileTitle(String projectName) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String formatted = localDateTime.format(formatter);
        return projectName + " - " + formatted;

    }

    private InputStreamContent buildMediaContent(String body) {
        InputStreamContent mediaContent = new InputStreamContent("text/csv",
                new BufferedInputStream(new ByteArrayInputStream(body.getBytes())));
        mediaContent.setLength(body.length());
        return mediaContent;
    }
}
