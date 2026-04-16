package com.deathdiary.utils

import android.content.Context
import android.net.Uri
import java.io.File

object MediaUtils {
    
    fun copyUriToInternalStorage(context: Context, uri: Uri, subDir: String = "media"): String? {
        return try {
            val mediaDir = File(context.filesDir, subDir)
            if (!mediaDir.exists()) {
                mediaDir.mkdirs()
            }
            val fileName = "img__"
            val extension = getExtension(context, uri)
            val destFile = File(mediaDir, ".")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                destFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun copyUrisToInternalStorage(context: Context, uris: List<Uri>, subDir: String = "media"): String {
        val paths = uris.mapNotNull { uri ->
            copyUriToInternalStorage(context, uri, subDir)
        }
        val gson = com.google.gson.Gson()
        return gson.toJson(paths)
    }
    
    private fun getExtension(context: Context, uri: Uri): String {
        return when (uri.scheme) {
            "content" -> {
                context.contentResolver.getType(uri)?.let { mimeType ->
                    android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
                } ?: "jpg"
            }
            "file" -> {
                File(uri.path ?: "").extension.ifEmpty { "jpg" }
            }
            else -> "jpg"
        }
    }
    
    fun isFileAccessible(filePath: String): Boolean {
        if (filePath.isBlank()) return false
        if (filePath.startsWith("content://") || filePath.startsWith("http")) return true
        return try {
            val file = File(filePath)
            file.exists() && file.canRead()
        } catch (e: Exception) {
            false
        }
    }
    
    fun pathToLoadableUri(filePath: String): Any {
        return if (filePath.startsWith("content://") || filePath.startsWith("http")) {
            filePath
        } else {
            File(filePath)
        }
    }
}
