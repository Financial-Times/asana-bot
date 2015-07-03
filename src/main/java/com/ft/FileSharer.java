package com.ft;

import com.ft.config.GoogleApiConfig;
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
        users.parallelStream().forEach(user -> shareWithUser(user, USER_TYPE));
        groups.parallelStream().forEach(user -> shareWithUser(user, GROUP_TYPE));

    }

    private void shareWithUser(String email, String type) {
        Permission newPermission = new Permission();
        newPermission.setValue(email).setType(type).setRole(WRITER_ROLE);

        try {
            drive.permissions().insert(file.getId(), newPermission)
                    .setSendNotificationEmails(false) //set to false to bypassed 51 requests per day limit
                    .execute();
        } catch (IOException e) {
            logger.warn("Could not share file with " + email, e);
        }
    }
}
