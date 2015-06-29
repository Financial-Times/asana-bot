package com.ft;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class FileSharer {

    private static final Logger logger = LoggerFactory.getLogger(FileSharer.class);

    public static final String USER_TYPE = "user";
    public static final String WRITER_ROLE = "writer";

    private final File file;
    private final List<String> emails;

    public FileSharer(File file, List<String> emails) {
        this.file = file;
        this.emails = emails;
    }

    public void share(Drive targetDrive) throws IOException {
        emails.parallelStream().forEach(user -> shareWithUser(user, targetDrive));

    }

    private void shareWithUser(String user, Drive drive) {
        Permission newPermission = new Permission();
        newPermission.setValue(user).setType(USER_TYPE).setRole(WRITER_ROLE);

        try {
            drive.permissions().insert(file.getId(), newPermission)
                    .setSendNotificationEmails(false) //set to false to bypassed 51 requests per day limit
                    .execute();
        } catch (IOException e) {
            logger.warn("Could not share file with " + user, e);
        }
    }
}
