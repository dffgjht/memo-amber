package com.deathdiary.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "community_posts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommunityPost(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val authorId: Long,
    val authorName: String,
    val authorAvatar: String = "", // URL 或资源ID
    val content: String,
    val timestamp: Long,
    val likes: Int = 0,
    val likedByCurrentUser: Boolean = false,
    val replyCount: Int = 0,
    val isPinned: Boolean = false,
    val category: String = "general", // general, memorial, sharing, support
    val tags: String = "" // 逗号分隔的标签
)

@Entity(
    tableName = "community_comments",
    foreignKeys = [
        ForeignKey(
            entity = CommunityPost::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommunityComment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val postId: Long,
    val authorId: Long,
    val authorName: String,
    val authorAvatar: String = "",
    val content: String,
    val timestamp: Long,
    val likes: Int = 0,
    val likedByCurrentUser: Boolean = false,
    val replyToCommentId: Long? = null, // 回复的评论ID
    val replyToAuthorName: String? = null // 回复的作者名
)

@Entity(tableName = "community_users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val avatar: String = "",
    val bio: String = "",
    val joinDate: Long = System.currentTimeMillis(),
    val postCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0
)
