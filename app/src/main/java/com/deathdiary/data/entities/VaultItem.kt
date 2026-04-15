package com.deathdiary.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_items")
data class VaultItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // accounts, documents, notes, etc.
    val content: String,
    val username: String = "",
    val password: String = "", // Should be encrypted
    val url: String = "",
    val timestamp: Long,
    val isEncrypted: Boolean = true
)
