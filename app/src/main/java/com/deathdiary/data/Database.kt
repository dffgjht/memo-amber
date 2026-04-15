package com.deathdiary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DiaryEntry::class,
        VaultItem::class,
        Will::class,
        MediaItem::class,
        CommunityPost::class,
        CommunityComment::class,
        User::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DeathDiaryDatabase : RoomDatabase() {
    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun vaultItemDao(): VaultItemDao
    abstract fun willDao(): WillDao
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun communityPostDao(): CommunityPostDao
    abstract fun communityCommentDao(): CommunityCommentDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: DeathDiaryDatabase? = null

        fun getDatabase(context: Context): DeathDiaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DeathDiaryDatabase::class.java,
                    "death_diary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
