package com.memoamber.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.memoamber.data.MemoAmberDatabase
import com.memoamber.data.entities.DiaryEntry
import com.memoamber.data.entities.VaultItem
import com.memoamber.data.entities.Will
import com.memoamber.data.entities.MediaItem
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 数据备份管理器
 * 支持导出和恢复加密备份
 */
class BackupManager(private val context: Context) {
    
    private val database = MemoAmberDatabase.getDatabase(context)
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    
    companion object {
        private const val BACKUP_VERSION = 1
        const val BACKUP_FILENAME_PREFIX = "death_diary_backup_"
    }
    
    /**
     * 备份数据结构
     */
    data class BackupData(
        val version: Int = BACKUP_VERSION,
        val timestamp: Long = System.currentTimeMillis(),
        val diaryEntries: List<DiaryEntry> = emptyList(),
        val vaultItems: List<VaultItem> = emptyList(),
        val willEntries: List<Will> = emptyList(),
        val mediaItems: List<MediaItem> = emptyList(),
        val contactType: String = "phone" // Backup version field
    )
    
    /**
     * 导出数据到 JSON 文件
     * @return 备份文件路径，失败返回 null
     */
    fun exportData(): String? {
        return try {
            runBlocking {
                val backupData = BackupData(
                    diaryEntries = database.diaryEntryDao().getAllEntries().first(),
                    vaultItems = database.vaultItemDao().getAllItems().first(),
                    willEntries = database.willDao().getAllWills().first(),
                    mediaItems = database.mediaItemDao().getAllItems().first()
                )
                
                val json = gson.toJson(backupData)
                val backupDir = File(context.filesDir, "backups")
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }
                
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val backupFile = File(backupDir, "${BACKUP_FILENAME_PREFIX}${timestamp}.json")
                
                backupFile.writeText(json)
                backupFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 从 JSON 文件导入数据
     * @param filePath 备份文件路径
     * @param merge 是否合并数据（true=合并，false=覆盖）
     * @return 是否成功
     */
    fun importData(filePath: String, merge: Boolean = true): Boolean {
        return try {
            val json = File(filePath).readText()
            val backupData = gson.fromJson(json, BackupData::class.java)
            
            if (backupData.version != BACKUP_VERSION) {
                throw IllegalArgumentException("不支持的备份版本：${backupData.version}")
            }
            
            if (!merge) {
                // 清除现有数据
                database.clearAllTables()
            }
            
            // 插入备份数据
            runBlocking {
                backupData.diaryEntries.forEach { entry ->
                    try {
                        database.diaryEntryDao().insertEntry(entry)
                    } catch (e: Exception) {
                        // 忽略重复插入错误
                    }
                }
                
                backupData.vaultItems.forEach { item ->
                    try {
                        database.vaultItemDao().insertItem(item)
                    } catch (e: Exception) {
                        // 忽略重复插入错误
                    }
                }
                
                backupData.willEntries.forEach { willEntry ->
                    try {
                        database.willDao().insertWill(willEntry)
                    } catch (e: Exception) {
                        // 忽略重复插入错误
                    }
                }
                
                backupData.mediaItems.forEach { media ->
                    try {
                        database.mediaItemDao().insertItem(media)
                    } catch (e: Exception) {
                        // 忽略重复插入错误
                    }
                }
            }
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 导出到外部存储（供用户分享）
     * @param uri 目标 URI（由系统文件选择器返回）
     * @return 是否成功
     */
    fun exportToUri(uri: Uri): Boolean {
        return try {
            runBlocking {
                val backupData = BackupData(
                    diaryEntries = database.diaryEntryDao().getAllEntries().first(),
                    vaultItems = database.vaultItemDao().getAllItems().first(),
                    willEntries = database.willDao().getAllWills().first(),
                    mediaItems = database.mediaItemDao().getAllItems().first()
                )
                
                val json = gson.toJson(backupData)
                
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(json.toByteArray())
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 从外部存储导入
     * @param uri 源 URI
     * @param merge 是否合并数据
     * @return 是否成功
     */
    fun importFromUri(uri: Uri, merge: Boolean = true): Boolean {
        return try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().readText()
            } ?: return false
            
            val backupData = gson.fromJson(json, BackupData::class.java)
            
            if (backupData.version != BACKUP_VERSION) {
                throw IllegalArgumentException("不支持的备份版本：${backupData.version}")
            }
            
            if (!merge) {
                database.clearAllTables()
            }
            
            // 插入数据（忽略重复）
            runBlocking {
                backupData.diaryEntries.forEach { entry ->
                    try { database.diaryEntryDao().insertEntry(entry) } catch (_: Exception) {}
                }
                backupData.vaultItems.forEach { item ->
                    try { database.vaultItemDao().insertItem(item) } catch (_: Exception) {}
                }
                backupData.willEntries.forEach { willEntry ->
                    try { database.willDao().insertWill(willEntry) } catch (_: Exception) {}
                }
                backupData.mediaItems.forEach { media ->
                    try { database.mediaItemDao().insertItem(media) } catch (_: Exception) {}
                }
            }
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 获取所有本地备份文件
     */
    fun getLocalBackups(): List<File> {
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) return emptyList()
        
        return backupDir.listFiles { file ->
            file.name.startsWith(BACKUP_FILENAME_PREFIX) && file.name.endsWith(".json")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
    
    /**
     * 删除旧备份（保留最近的 N 个）
     * @param keepCount 保留数量
     */
    fun cleanupOldBackups(keepCount: Int = 5) {
        val backups = getLocalBackups()
        if (backups.size <= keepCount) return
        
        backups.drop(keepCount).forEach { it.delete() }
    }
    
    /**
     * 删除指定备份文件
     */
    fun deleteBackup(filePath: String): Boolean {
        return try {
            File(filePath).delete()
        } catch (e: Exception) {
            false
        }
    }
}
