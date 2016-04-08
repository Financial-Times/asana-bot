package com.ft.backup.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

public class FileFinder {

    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
    private final Drive drive;

    public FileFinder(@NotNull Drive drive) {
        this.drive = drive;
    }

    public File findOrCreateRootFolder(String root) throws IOException {
        File folder = findFolder(root);
        if (folder != null) {
            return folder;
        }
        return createFolder(root);
    }

    public File findFolder(String root) throws IOException {
        FileList candidates = drive.files().list()
                .setQ(buildQuery(root))
                .setPageSize(1)
                .execute();
        List<File> folders = candidates.getFiles();

        if (!folders.isEmpty()) {
            return folders.get(0);
        }

        return null;
    }

    private File createFolder(String root) throws IOException {
        File folder = new File().setName(root);
        folder.setMimeType(FOLDER_MIME_TYPE);
        Drive.Files.Create request = drive.files().create(folder);
        folder = request.execute();

        return folder;
    }

    private String buildQuery(String root) {
        return "name = '" + root + "' and mimeType = '" + FOLDER_MIME_TYPE + "'";
    }
}
