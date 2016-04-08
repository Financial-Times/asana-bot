package com.ft;

import com.ft.config.GoogleApiConfig;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FileSharer {

    private static final Logger logger = LoggerFactory.getLogger(FileSharer.class);

    public static final String USER_TYPE = "user";
    public static final String GROUP_TYPE = "group";
    public static final String WRITER_ROLE = "writer";

    private final Drive drive;
    private final File file;
    private final List<String> users;
    private final List<String> groups;

    private final JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
        @Override
        public void onFailure(GoogleJsonError e,
                              HttpHeaders responseHeaders)
                throws IOException {
            logger.warn("Could not share file " + file.getName());
            logger.warn(e.getMessage());
        }

        @Override
        public void onSuccess(Permission permission,
                              HttpHeaders responseHeaders)
                throws IOException {
            if (logger.isDebugEnabled())
                logger.debug("Successfully shared file " + file.getName());
        }
    };

    public FileSharer(Drive drive, File file, GoogleApiConfig.SharedWith sharedWith) {
        this.drive = drive;
        this.file = file;
        this.users = getOrDefault(sharedWith.getUsers());
        this.groups = getOrDefault(sharedWith.getGroups());
    }

    private static List<String> getOrDefault(List<String> emails) {
        return emails != null ? emails : Collections.emptyList();
    }

    public void share() throws IOException {
        BatchRequest batch = drive.batch();
        users.parallelStream().forEach(user -> shareWithUser(batch, user, USER_TYPE));
        groups.parallelStream().forEach(user -> shareWithUser(batch, user, GROUP_TYPE));
        batch.execute();
    }

    private void shareWithUser(BatchRequest batch, String email, String type) {
        Permission newPermission = new Permission();
        newPermission.setType(type).setRole(WRITER_ROLE).setEmailAddress(email);

        try {
            drive.permissions().create(file.getId(), newPermission)
                    .setSendNotificationEmail(false) //set to false to bypassed 51 requests per day limit
                    .setFields("id")
                    .queue(batch, callback);
        } catch (IOException e) {
            logger.warn("Could not share file with " + email, e);
        }
    }
}
