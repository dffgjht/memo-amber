package com.deathdiary.network

import com.deathdiary.data.entities.CommunityPost
import com.deathdiary.data.entities.CommunityComment
import com.deathdiary.data.entities.User
import retrofit2.Response
import retrofit2.http.*

/**
 * API 服务接口
 * 基础 URL: https://api.deathdiary.com/v1
 */
interface ApiService {
    
    // ========== 用户认证 ==========
    
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
    
    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<LogoutResponse>
    
    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<User>
    
    // ========== 社区帖子 ==========
    
    @GET("posts")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null
    ): Response<PostListResponse>
    
    @GET("posts/{id}")
    suspend fun getPost(
        @Path("id") postId: Long
    ): Response<CommunityPost>
    
    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body request: CreatePostRequest
    ): Response<CommunityPost>
    
    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") postId: Long,
        @Header("Authorization") token: String,
        @Body request: UpdatePostRequest
    ): Response<CommunityPost>
    
    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Path("id") postId: Long,
        @Header("Authorization") token: String
    ): Response<DeleteResponse>
    
    @POST("posts/{id}/like")
    suspend fun likePost(
        @Path("id") postId: Long,
        @Header("Authorization") token: String
    ): Response<LikeResponse>
    
    @DELETE("posts/{id}/like")
    suspend fun unlikePost(
        @Path("id") postId: Long,
        @Header("Authorization") token: String
    ): Response<LikeResponse>
    
    // ========== 评论 ==========
    
    @GET("posts/{postId}/comments")
    suspend fun getComments(
        @Path("postId") postId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<CommentListResponse>
    
    @POST("posts/{postId}/comments")
    suspend fun createComment(
        @Path("postId") postId: Long,
        @Header("Authorization") token: String,
        @Body request: CreateCommentRequest
    ): Response<CommunityComment>
    
    @DELETE("comments/{id}")
    suspend fun deleteComment(
        @Path("id") commentId: Long,
        @Header("Authorization") token: String
    ): Response<DeleteResponse>
    
    // ========== 数据同步 ==========
    
    @GET("sync/data")
    suspend fun syncData(
        @Header("Authorization") token: String,
        @Query("lastSyncTime") lastSyncTime: Long? = null
    ): Response<SyncDataResponse>
    
    @POST("sync/data")
    suspend fun uploadData(
        @Header("Authorization") token: String,
        @Body data: SyncUploadRequest
    ): Response<SyncUploadResponse>
    
    // ========== 用户资料 ==========
    
    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") userId: Long
    ): Response<User>
    
    @PUT("users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<User>
    
    @POST("users/{id}/follow")
    suspend fun followUser(
        @Path("id") userId: Long,
        @Header("Authorization") token: String
    ): Response<FollowResponse>
    
    @DELETE("users/{id}/follow")
    suspend fun unfollowUser(
        @Path("id") userId: Long,
        @Header("Authorization") token: String
    ): Response<FollowResponse>
}

// ========== 请求/响应模型 ==========

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val token: String,
    val user: User
)

data class LogoutResponse(
    val success: Boolean
)

data class CreatePostRequest(
    val content: String,
    val category: String,
    val tags: String
)

data class UpdatePostRequest(
    val content: String
)

data class PostListResponse(
    val posts: List<CommunityPost>,
    val total: Int,
    val page: Int,
    val totalPages: Int
)

data class CommentListResponse(
    val comments: List<CommunityComment>,
    val total: Int
)

data class CreateCommentRequest(
    val content: String,
    val replyToCommentId: Long? = null
)

data class LikeResponse(
    val success: Boolean,
    val likes: Int
)

data class DeleteResponse(
    val success: Boolean
)

data class SyncDataResponse(
    val posts: List<CommunityPost>,
    val comments: List<CommunityComment>,
    val lastSyncTime: Long
)

data class SyncUploadRequest(
    val posts: List<CommunityPost>,
    val comments: List<CommunityComment>
)

data class SyncUploadResponse(
    val success: Boolean,
    val syncedAt: Long
)

data class UpdateProfileRequest(
    val bio: String?,
    val avatar: String?
)

data class FollowResponse(
    val success: Boolean,
    val following: Boolean
)
