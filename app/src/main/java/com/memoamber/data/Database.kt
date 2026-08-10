package com.memoamber.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.memoamber.data.entities.DiaryEntry
import com.memoamber.data.entities.VaultItem
import com.memoamber.data.entities.Will
import com.memoamber.data.entities.MediaItem
import com.memoamber.data.entities.CommunityPost
import com.memoamber.data.entities.CommunityComment
import com.memoamber.data.entities.User

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
    version = 3,
    exportSchema = false
)
abstract class MemoAmberDatabase : RoomDatabase() {
    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun vaultItemDao(): VaultItemDao
    abstract fun willDao(): WillDao
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun communityPostDao(): CommunityPostDao
    abstract fun communityCommentDao(): CommunityCommentDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: MemoAmberDatabase? = null

        fun getDatabase(context: Context): MemoAmberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoAmberDatabase::class.java,
                    "death_diary_database"
                )
                    // 开发阶段 schema 仍在演进，暂以清库重建避免升级崩溃。
                    // 正式发布前应替换为显式 Migration 以保留用户数据。
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
