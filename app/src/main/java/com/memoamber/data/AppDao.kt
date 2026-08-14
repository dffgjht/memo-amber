package com.memoamber.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.memoamber.data.entities.DiaryEntry
import com.memoamber.data.entities.VaultItem
import com.memoamber.data.entities.Will
import com.memoamber.data.entities.MediaItem
import com.memoamber.data.entities.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM diary_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries ORDER BY timestamp DESC")
    suspend fun getAllEntriesSync(): List<DiaryEntry>

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): DiaryEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntry): Long

    @Update
    suspend fun updateEntry(entry: DiaryEntry)

    @Delete
    suspend fun deleteEntry(entry: DiaryEntry)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)
}

@Dao
interface VaultItemDao {
    @Query("SELECT * FROM vault_items ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items ORDER BY timestamp DESC")
    suspend fun getAllItemsSync(): List<VaultItem>

    @Query("SELECT * FROM vault_items WHERE id = :id")
    suspend fun getItemById(id: Long): VaultItem?

    @Query("SELECT * FROM vault_items WHERE category = :category ORDER BY timestamp DESC")
    fun getItemsByCategory(category: String): Flow<List<VaultItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: VaultItem): Long

    @Update
    suspend fun updateItem(item: VaultItem)

    @Delete
    suspend fun deleteItem(item: VaultItem)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)
}

@Dao
interface WillDao {
    @Query("SELECT * FROM wills ORDER BY timestamp DESC")
    fun getAllWills(): Flow<List<Will>>

    @Query("SELECT * FROM wills WHERE id = :id")
    suspend fun getWillById(id: Long): Will?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWill(will: Will): Long

    @Update
    suspend fun updateWill(will: Will)

    @Delete
    suspend fun deleteWill(will: Will)

    @Query("DELETE FROM wills WHERE id = :id")
    suspend fun deleteWillById(id: Long)

    @Query("UPDATE wills SET is_released = 1 WHERE id = :id")
    suspend fun markAsReleased(id: Long)

    @Query("SELECT * FROM wills ORDER BY timestamp DESC")
    suspend fun getAllWillsSync(): List<Will>

    @Query("SELECT * FROM wills WHERE id = :id")
    suspend fun getWillByIdSync(id: Long): Will?
}

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM media_items ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items ORDER BY timestamp DESC")
    suspend fun getAllItemsSync(): List<MediaItem>

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getItemById(id: Long): MediaItem?

    @Query("SELECT * FROM media_items WHERE type = :type ORDER BY timestamp DESC")
    fun getItemsByType(type: String): Flow<List<MediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MediaItem): Long

    @Update
    suspend fun updateItem(item: MediaItem)

    @Delete
    suspend fun deleteItem(item: MediaItem)

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name COLLATE NOCASE ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts ORDER BY name COLLATE NOCASE ASC")
    suspend fun getAllContactsSync(): List<Contact>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContactById(id: Long)
}
