package com.deathdiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deathdiary.data.entities.CommunityPost
import com.deathdiary.data.entities.CommunityComment
import com.deathdiary.data.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityPostDao {
    @Query("SELECT * FROM community_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<CommunityPost>>

    @Query("SELECT * FROM community_posts WHERE id = :postId")
    suspend fun getPostById(postId: Long): CommunityPost?

    @Query("SELECT * FROM community_posts WHERE category = :category ORDER BY timestamp DESC")
    fun getPostsByCategory(category: String): Flow<List<CommunityPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CommunityPost): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<CommunityPost>)

    @Query("UPDATE community_posts SET likes = likes + 1 WHERE id = :postId")
    suspend fun incrementLikes(postId: Long)

    @Query("UPDATE community_posts SET likes = likes - 1 WHERE id = :postId")
    suspend fun decrementLikes(postId: Long)

    @Query("DELETE FROM community_posts WHERE id = :postId")
    suspend fun deletePost(postId: Long)
}

@Dao
interface CommunityCommentDao {
    @Query("SELECT * FROM community_comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Long): Flow<List<CommunityComment>>

    @Query("SELECT * FROM community_comments WHERE id = :commentId")
    suspend fun getCommentById(commentId: Long): CommunityComment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommunityComment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommunityComment>)

    @Query("UPDATE community_comments SET likes = likes + 1 WHERE id = :commentId")
    suspend fun incrementLikes(commentId: Long)

    @Query("UPDATE community_comments SET likes = likes - 1 WHERE id = :commentId")
    suspend fun decrementLikes(commentId: Long)

    @Query("DELETE FROM community_comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Long)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("UPDATE users SET postCount = postCount + 1 WHERE id = :userId")
    suspend fun incrementPostCount(userId: Long)

    @Query("UPDATE users SET followerCount = followerCount + 1 WHERE id = :userId")
    suspend fun incrementFollowerCount(userId: Long)

    @Query("UPDATE users SET followingCount = followingCount + 1 WHERE id = :userId")
    suspend fun incrementFollowingCount(userId: Long)
}
