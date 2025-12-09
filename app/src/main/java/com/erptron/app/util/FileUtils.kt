package com.positron.teachers.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

class FileUtils {
    companion object {
        fun getPath(context: Context, uri: Uri): String? {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                val path = cursor.getString(columnIndex)
                cursor.close()
                return path
            }
            return null
        }
    }
}