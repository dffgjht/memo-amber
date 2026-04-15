package com.deathdiary.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wills")
data class Will(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val recipientName: String,
    val recipientContact: String,
    val releaseCondition: String, // date, trigger, etc.
    val releaseDate: Long? = null, // Timestamp for scheduled release
    val isReleased: Boolean = false,
    val timestamp: Long,
    val isEncrypted: Boolean = true
)
