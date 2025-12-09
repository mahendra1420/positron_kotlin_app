package com.positron.teachers.drive;
// [START drive_create_drive]

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/* class to demonstrate use-case of Drive's create drive. */
public class CreateDrive {

  /**
   * Create a drive.
   *
   * @return Newly created drive id.
   * @throws IOException if service account credentials file not found.
   */
  public static String createDrive() throws IOException {

    GoogleCredentials credentials =
        GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE));
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Build a new authorized API client service.
    com.google.api.services.drive.Drive service =
        new com.google.api.services.drive.Drive.Builder(new NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer)
            .setApplicationName("Drive samples")
            .build();

    Drive driveMetadata = new Drive();
    driveMetadata.setName("Project Resources");
    String requestId = UUID.randomUUID().toString();
    try {
      Drive drive = service.drives().create(requestId,
              driveMetadata)
          .execute();
      System.out.println("Drive ID: " + drive.getId());

      return drive.getId();
    } catch (GoogleJsonResponseException e) {
      // TODO(developer) - handle error appropriately
      System.err.println("Unable to create drive: " + e.getDetails());
      throw e;
    }
  }
}
// [END drive_create_drive]

