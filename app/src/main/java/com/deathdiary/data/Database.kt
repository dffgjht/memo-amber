package com.deathdiary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.deathdiary.data.entities.DiaryEntry
import com.deathdiary.data.entities.VaultItem
import com.deathdiary.data.entities.Will
import com.deathdiary.data.entities.MediaItem
import com.deathdiary.data.entities.CommunityPost
import com.deathdiary.data.entities.CommunityComment
import com.deathdiary.data.entities.User

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

        /**
         * 数据库迁移：v2 → v3
         * - 密码哈希格式从简单 hashCode() 升级为 BCrypt
         * - 需要清除旧的密码哈希，用户需要重新设置主密码
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 清除旧的密码哈希（格式不兼容 BCrypt）
                // SharedPreferences 中的密码需要用户重新设置
                // 这里只处理数据库层面的迁移
                
                // 如果有需要迁移的数据库表结构变更，在这里添加
                // 当前版本没有表结构变更，只是密码哈希算法变更
            }
        }

        fun getDatabase(context: Context): MemoAmberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoAmberDatabase::class.java,
                    "memo_amber_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
