package com.memoamber.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wills")
data class Will(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    @ColumnInfo(name = "recipient_name")
    val recipientName: String,
    @ColumnInfo(name = "recipient_contact")
    val recipientContact: String,
    @ColumnInfo(name = "contact_type")
    val contactType: String = "phone", // "phone" or "email"
    @ColumnInfo(name = "release_condition")
    val releaseCondition: String, // date, trigger, etc.
    @ColumnInfo(name = "release_date")
    val releaseDate: Long? = null, // Timestamp for scheduled release
    @ColumnInfo(name = "is_released")
    val isReleased: Boolean = false,
    val timestamp: Long,
    @ColumnInfo(name = "is_encrypted")
    val isEncrypted: Boolean = true
)
