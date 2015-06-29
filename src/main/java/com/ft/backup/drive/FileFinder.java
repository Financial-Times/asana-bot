package com.ft.backup.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

public class FileFinder {

    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
    private static final String SEARCH_FOLDER_QUERY = "title = 'asana_backups' and mimeType = '" + FOLDER_MIME_TYPE + "'";
    private static final String BACKUP_FOLDER = "asana_backups";
    private final Drive drive;

    public FileFinder(@NotNull Drive drive) {
        this.drive = drive;
    }

    public File findOrCreateRootFolder() throws IOException {
        FileList candidates = drive.files().list()
                .setQ(SEARCH_FOLDER_QUERY)
                .setMaxResults(1)
                .execute();
        List<File> folders = candidates.getItems();

        if (!folders.isEmpty()) {
            return folders.get(0);
        }

        File folder = new File().setTitle(BACKUP_FOLDER);
        folder.setMimeType(FOLDER_MIME_TYPE);
        Drive.Files.Insert request = drive.files().insert(folder);
        folder = request.execute();

        return folder;
    }
}
