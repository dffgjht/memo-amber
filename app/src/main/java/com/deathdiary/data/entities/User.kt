package com.deathdiary.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String = "",
    val passwordHash: String = "",
    val avatar: String = "",
    val bio: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false,
    val lastSyncTime: Long? = null,
    val token: String? = null,
    val joinDate: Long = System.currentTimeMillis(),
    val postCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0
)
