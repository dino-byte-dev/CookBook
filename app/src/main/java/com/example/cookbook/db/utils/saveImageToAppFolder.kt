package com.example.cookbook.db.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import okio.use
import java.io.File
import java.io.FileOutputStream

fun saveImageToAppFolder(context: Context, uri: Uri): String? {
    return try {
        // 1. Einen eigenen Unterordner im internen Speicher anlegen ("recipe_images")
        val imagesFolder = File(context.filesDir, "recipe_images")
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs()
        }

        // 2. Eine neue, eindeutige Datei mit Zeitstempel anlegen
        val destinationFile = File(imagesFolder, "recipe_${System.currentTimeMillis()}.jpg")

        // 3. Den Stream vom Photo-Picker lesen und in unsere neue Datei schreiben
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // 4. Den absoluten Pfad der neuen Datei zurückgeben
        Log.d("destinationFile.absolutePath", destinationFile.absolutePath)
        destinationFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}