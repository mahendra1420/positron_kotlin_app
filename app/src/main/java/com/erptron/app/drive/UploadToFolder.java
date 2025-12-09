package com.positron.teachers.drive;

// [START drive_upload_to_folder]

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/* Class to demonstrate Drive's upload to folder use-case. */
public class UploadToFolder {

  /**
   * Upload a file to the specified folder.
   *
   * @param realFolderId Id of the folder.
   * @return Inserted file metadata if successful, {@code null} otherwise.
   * @throws IOException if service account credentials file not found.
   */
  public static File uploadToFolder(String realFolderId) throws IOException {

    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Build a new authorized API client service.
    Drive service = new Drive.Builder(new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
        .setApplicationName("Drive samples")
        .build();

    // File's metadata.
    File fileMetadata = new File();
    fileMetadata.setName("photo.jpg");
    fileMetadata.setParents(Collections.singletonList(realFolderId));
    java.io.File filePath = new java.io.File("files/photo.jpg");
    FileContent mediaContent = new FileContent("image/jpeg", filePath);
    try {
      File file = service.files().create(fileMetadata, mediaContent)
          .setFields("id, parents")
          .execute();
      System.out.println("File ID: " + file.getId());
      return file;
    } catch (GoogleJsonResponseException e) {

      System.err.println("Unable to upload file: " + e.getDetails());
      throw e;
    }
  }
}
// [END drive_upload_to_folder]
