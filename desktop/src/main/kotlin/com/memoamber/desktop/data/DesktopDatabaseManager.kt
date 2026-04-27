package com.memoamber.desktop.data

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 桌面端数据库 (JDBC + SQLite)
 * 数据存储在 ~/.memoamber/data.db
 */
class DesktopDatabaseManager {

    private val dataDir = File(System.getProperty("user.home"), ".memoamber")
    private val dbFile = File(dataDir, "data.db")
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private var conn: Connection? = null

    init {
        dataDir.mkdirs()
        initTables()
    }

    private fun db(): Connection =
        conn ?: DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").also { conn = it }

    private fun initTables() {
        db().createStatement().use { s ->
            s.execute("CREATE TABLE IF NOT EXISTS diary_entries (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, mood TEXT DEFAULT '', location TEXT DEFAULT '', created_at TEXT NOT NULL, updated_at TEXT NOT NULL)")
            s.execute("CREATE TABLE IF NOT EXISTS vault_items (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, username TEXT NOT NULL, encrypted_password TEXT NOT NULL, url TEXT DEFAULT '', notes TEXT DEFAULT '', category TEXT DEFAULT 'general', created_at TEXT NOT NULL, updated_at TEXT NOT NULL)")
            s.execute("CREATE TABLE IF NOT EXISTS wills (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, status TEXT DEFAULT 'draft', recipient TEXT DEFAULT '', send_method TEXT DEFAULT 'none', created_at TEXT NOT NULL, updated_at TEXT NOT NULL)")
            s.execute("CREATE TABLE IF NOT EXISTS media_items (id INTEGER PRIMARY KEY AUTOINCREMENT, file_path TEXT NOT NULL, description TEXT DEFAULT '', media_type TEXT DEFAULT 'image', created_at TEXT NOT NULL)")
            s.execute("CREATE TABLE IF NOT EXISTS community_posts (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT NOT NULL, author TEXT DEFAULT '匿名', category TEXT DEFAULT 'general', tags TEXT DEFAULT '', likes INTEGER DEFAULT 0, created_at TEXT NOT NULL)")
        }
    }

    private fun now() = LocalDateTime.now().format(fmt)

    // 日记
    fun insertDiary(title: String, content: String, mood: String = "", location: String = ""): Long =
        insert("diary_entries", "title, content, mood, location, created_at, updated_at", title, content, mood, location, now(), now())
    fun getAllDiaries(): List<Map<String, String>> = query("SELECT * FROM diary_entries ORDER BY created_at DESC")
    fun deleteDiary(id: Long) = delete("diary_entries", id)

    // 密码箱
    fun insertVaultItem(title: String, username: String, encPwd: String, url: String = "", notes: String = ""): Long =
        insert("vault_items", "title, username, encrypted_password, url, notes, category, created_at, updated_at", title, username, encPwd, url, notes, "general", now(), now())
    fun getAllVaultItems(): List<Map<String, String>> = query("SELECT * FROM vault_items ORDER BY created_at DESC")
    fun deleteVaultItem(id: Long) = delete("vault_items", id)

    // 遗嘱
    fun insertWill(title: String, content: String, recipient: String = ""): Long =
        insert("wills", "title, content, recipient, send_method, created_at, updated_at", title, content, recipient, "none", now(), now())
    fun getAllWills(): List<Map<String, String>> = query("SELECT * FROM wills ORDER BY created_at DESC")
    fun deleteWill(id: Long) = delete("wills", id)

    // 相册
    fun insertMedia(path: String, desc: String = "", type: String = "image"): Long =
        insert("media_items", "file_path, description, media_type, created_at", path, desc, type, now())
    fun getAllMedia(): List<Map<String, String>> = query("SELECT * FROM media_items ORDER BY created_at DESC")
    fun deleteMedia(id: Long) = delete("media_items", id)

    // 社区
    fun insertPost(content: String, author: String = "匿名"): Long =
        insert("community_posts", "content, author, category, tags, created_at", content, author, "general", "", now())
    fun getAllPosts(): List<Map<String, String>> = query("SELECT * FROM community_posts ORDER BY created_at DESC")

    // 通用
    private fun insert(table: String, columns: String, vararg values: String): Long {
        val placeholders = values.joinToString(",") { "?" }
        db().prepareStatement("INSERT INTO $table ($columns) VALUES ($placeholders)", PreparedStatement.RETURN_GENERATED_KEYS).use { ps ->
            values.forEachIndexed { i, v -> ps.setString(i + 1, v) }
            ps.executeUpdate()
            ps.generatedKeys.use { rs -> if (rs.next()) return rs.getLong(1) }
        }
        return -1
    }

    private fun query(sql: String): List<Map<String, String>> {
        val results = mutableListOf<Map<String, String>>()
        db().createStatement().use { stmt ->
            stmt.executeQuery(sql).use { rs ->
                while (rs.next()) {
                    val map = mutableMapOf<String, String>()
                    for (i in 1..rs.metaData.columnCount) map[rs.metaData.getColumnName(i)] = rs.getString(i) ?: ""
                    results.add(map)
                }
            }
        }
        return results
    }

    private fun delete(table: String, id: Long): Boolean {
        db().prepareStatement("DELETE FROM $table WHERE id = ?").use { ps ->
            ps.setLong(1, id)
            return ps.executeUpdate() > 0
        }
    }

    fun close() { conn?.close(); conn = null }
}
