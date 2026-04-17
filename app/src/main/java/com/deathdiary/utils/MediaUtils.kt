package com.deathdiary.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 媒体文件工具类
 * 提供文件复制、路径验证等功能
 */
object MediaUtils {
    
    /**
     * 复制单个 URI 到内部存储
     * @param context 上下文
     * @param uri 源文件 URI
     * @param subDir 子目录名称
     * @return 复制后的文件路径，失败返回 null
     */
    fun copyUriToInternalStorage(context: Context, uri: Uri, subDir: String = "media"): String? {
        return try {
            val mediaDir = File(context.filesDir, subDir)
            if (!mediaDir.exists()) {
                mediaDir.mkdirs()
            }
            
            val fileName = generateUniqueFileName(context, uri)
            val destFile = File(mediaDir, fileName)
            
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
    
    /**
     * 批量复制多个 URI 到内部存储
     * @param context 上下文
     * @param uris 源文件 URI 列表
     * @param subDir 子目录名称
     * @return JSON 格式的路径数组
     */
    fun copyUrisToInternalStorage(context: Context, uris: List<Uri>, subDir: String = "media"): String {
        val paths = uris.mapNotNull { uri ->
            copyUriToInternalStorage(context, uri, subDir)
        }
        val gson = com.google.gson.Gson()
        return gson.toJson(paths)
    }
    
    /**
     * 生成唯一的文件名（使用时间戳）
     */
    private fun generateUniqueFileName(context: Context, uri: Uri): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val extension = getExtension(context, uri)
        return "IMG_${timestamp}.${extension}"
    }
    
    /**
     * 获取文件扩展名
     */
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
    
    /**
     * 检查文件是否可访问
     * @param filePath 文件路径或 URI
     * @return 是否可访问
     */
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
    
    /**
     * 将文件路径转换为可加载的 URI 或 File 对象
     * @param filePath 文件路径
     * @return 可加载的对象（String URI 或 File）
     */
    fun pathToLoadableUri(filePath: String): Any {
        return if (filePath.startsWith("content://") || filePath.startsWith("http")) {
            filePath
        } else {
            File(filePath)
        }
    }
    
    /**
     * 删除媒体文件
     * @param context 上下文
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    fun deleteMediaFile(context: Context, filePath: String): Boolean {
        return try {
            if (filePath.startsWith("content://")) {
                // Content URI 删除需要特殊处理
                val uri = Uri.parse(filePath)
                context.contentResolver.delete(uri, null, null) > 0
            } else {
                val file = File(filePath)
                if (file.exists()) {
                    file.delete()
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 获取文件大小（字节）
     */
    fun getFileSize(filePath: String): Long {
        return try {
            if (filePath.startsWith("content://") || filePath.startsWith("http")) {
                0L
            } else {
                File(filePath).length()
            }
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * 清理过期的临时文件
     * @param context 上下文
     * @param maxAgeHours 最大保存时间（小时）
     */
    fun cleanupOldFiles(context: Context, maxAgeHours: Long = 24) {
        try {
            val mediaDir = File(context.filesDir, "media")
            if (!mediaDir.exists()) return
            
            val currentTime = System.currentTimeMillis()
            val maxAgeMillis = maxAgeHours * 60 * 60 * 1000
            
            mediaDir.listFiles()?.forEach { file ->
                if (currentTime - file.lastModified() > maxAgeMillis) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
