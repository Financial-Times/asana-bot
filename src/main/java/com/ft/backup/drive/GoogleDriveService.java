package com.ft.backup.drive;

import com.ft.FileSharer;
import com.ft.asanaapi.model.ProjectInfo;
import com.ft.config.GoogleApiConfig;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleDriveService {

    private static final List<String> DRIVE_SCOPES =  Collections.singletonList(DriveScopes.DRIVE);
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final JacksonFactory jsonFactory = new JacksonFactory();

    @Autowired private GoogleApiConfig googleApiConfig;

    @Getter @Setter
    private Drive drive;

    @PostConstruct
    public void connectToDrive() throws GeneralSecurityException, IOException {
        InputStream in = googleApiConfig.toInputStream();
        GoogleCredential credential = GoogleCredential.fromStream(in, httpTransport, jsonFactory)
                .createScoped(DRIVE_SCOPES);
        this.drive = new Drive.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(credential).build();
    }

    public void uploadProjectFile(ProjectInfo project, File folder, String body) throws IOException {
        CsvFileUploader csvFileUploader = new CsvFileUploader(drive, project.getName(), folder, body);
        File uploaded = csvFileUploader.upload();

        FileSharer fileSharer = new FileSharer(uploaded, googleApiConfig.getShareWithUsers());
        fileSharer.share(drive);
    }

    public File findOrCreateRootFolder() throws IOException {
        File folder = new FileFinder(drive).findOrCreateRootFolder();
        FileSharer fileSharer = new FileSharer(folder, googleApiConfig.getShareWithUsers());
        fileSharer.share(drive);
        return folder;
    }

    public void removeFilesOlderThan(LocalDateTime dateTimeFrom) throws IOException{
        new FileRemover(drive).removeFilesOlderThan(dateTimeFrom);
    }
}
