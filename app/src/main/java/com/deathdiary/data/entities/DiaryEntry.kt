package com.deathdiary.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val mood: String, // happy, sad, neutral, etc.
    val timestamp: Long,
    val isEncrypted: Boolean = true,
    val mediaPaths: String = "", // JSON array of media file paths
    val locationName: String? = null, // Location name (e.g., "Central Park")
    val latitude: Double? = null, // Latitude for map viewing
    val longitude: Double? = null // Longitude for map viewing
)
