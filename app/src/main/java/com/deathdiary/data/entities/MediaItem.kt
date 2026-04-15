package com.deathdiary.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val filePath: String,
    val type: String, // image, video, audio
    val timestamp: Long,
    val tags: String = "" // Comma-separated tags
)
