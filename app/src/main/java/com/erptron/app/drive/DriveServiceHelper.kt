package com.positron.teachers.drive

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File

import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors

class DriveServiceHelper(drive: Drive) {

    private val executor = Executors.newSingleThreadExecutor()
    private var drive: Drive = drive

    fun uploadFile(file: java.io.File, fileCont: File, update: String): Task<String> {

        return Tasks.call(executor) {
            val fileContent = FileContent("application/*", file)
            var myfile: File? = null

            try {
                myfile = if (update.isNotEmpty()) {
                    drive.files().update(update, fileCont, fileContent).execute()
                } else {
                    drive.files().create(fileCont, fileContent).execute()
                }
            } catch (e: Exception) {

            }

            if (myfile == null) {
                throw IOException()
            }

            return@call myfile.id
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Throws(IOException::class)
    /*fun createFile(name: String, mimeType: String, file: File, inputStream: InputStream): OutputStream {
        val fileMetadata = File()
        fileMetadata.name = name
        val mediaContent = FileContent(mimeType, file)
        val file = drive.files().create(fileMetadata, mediaContent)
            .setFields("id")
            .execute()

        Log.d("DriveServiceHelper", "File ID: ${file.id}")

        return OutputStream.nullOutputStream()
    }*/

    fun uploadToFolder(context: Context, realFolderId: String, filePath: String): File? {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_FILE)
        )

        val httpTransport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val drive = Drive.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName("Drive samples")
            .build()

        val fileMetadata = File()
            .setName(filePath.substringAfterLast("/")) // Get file name from path
            .setParents(Collections.singletonList(realFolderId))

        val mediaContent = FileContent(
            context.contentResolver.getType(Uri.parse(filePath)) ?: "application/octet-stream",
            //File(filePath)
                java.io.File(filePath)
        )

        return try {
            val file = drive.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute()
            println("File ID: ${file.id}")
            file
        } catch (e: GoogleJsonResponseException) {
            println("Unable to upload file: ${e.details}")
            null
        }
    }


}