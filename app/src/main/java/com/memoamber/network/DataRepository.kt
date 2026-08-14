package com.memoamber.network

import com.memoamber.data.entities.CommunityPost
import com.memoamber.data.entities.CommunityComment
import com.memoamber.data.entities.User
import retrofit2.Response

/**
 * 数据仓库
 * 负责管理本地数据和远程数据的同步
 */
class DataRepository(private val apiService: ApiService) {

    /**
     * 用户注册
     */
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            val response = apiService.register(
                RegisterRequest(username, email, password)
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    body.user?.let { Result.success(it) }
                        ?: Result.failure(Exception("响应数据为空"))
                } else {
                    Result.failure(Exception(body?.message ?: "请求失败"))
                }
            } else {
                Result.failure(Exception(response.message() ?: "请求失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 用户登录
     */
    suspend fun login(
        username: String,
        password: String
    ): Result<Pair<User, String>> {
        return try {
            val response = apiService.login(
                LoginRequest(username, password)
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val user = body.user
                    val token = body.token
                    if (user != null && token != null) {
                        Result.success(Pair(user, token))
                    } else {
                        Result.failure(Exception("登录响应数据不完整"))
                    }
                } else {
                    Result.failure(Exception(body?.message ?: "登录失败"))
                }
            } else {
                Result.failure(Exception(response.message() ?: "请求失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取帖子列表
     */
    suspend fun getPosts(
        page: Int = 1,
        category: String? = null
    ): Result<List<CommunityPost>> {
        return try {
            val response = apiService.getPosts(page, limit = 20, category)
            if (response.isSuccessful) {
                Result.success(response.body()?.posts ?: emptyList())
            } else {
                Result.failure(Exception(response.message() ?: "请求失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 创建帖子
     */
    suspend fun createPost(
        token: String,
        content: String,
        category: String,
        tags: String
    ): Result<CommunityPost> {
        return try {
            val api = RetrofitClient.createAuthenticatedApiService(token)
            val response = api.createPost(
                "Bearer $token",
                CreatePostRequest(content, category, tags)
            )
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("响应数据为空"))
            } else {
                Result.failure(Exception(response.message() ?: "请求失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 点赞帖子
     */
    suspend fun likePost(
        token: String,
        postId: Long
    ): Result<Int> {
        return try {
            val api = RetrofitClient.createAuthenticatedApiService(token)
            val response = api.likePost(postId, token)
            if (response.isSuccessful) {
                Result.success(response.body()?.likes ?: 0)
            } else {
                Result.failure(Exception(response.message() ?: "请求失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 取消点赞
     */
    suspend fun unlikePost(
        token: String,
        postId: Long
    ): Result<Int> {
        return try {
            val api = RetrofitClient.createAuthenticatedApiService(token)
            val response = api.unlikePost(postId, token)
            if (response.isSuccessful) {
                Result.success(response.body()?.likes ?: 0)
            } else {
                Result.failure(Exception(response.message() ?: "请求失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取评论
     */
    suspend fun getComments(
        postId: Long,
        page: Int = 1
    ): Result<List<CommunityComment>> {
        return try {
            val response = apiService.getComments(postId, page)
            if (response.isSuccessful) {
                Result.success(response.body()?.comments ?: emptyList())
            } else {
                Result.failure(Exception(response.message() ?: "请求失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 创建评论
     */
    suspend fun createComment(
        token: String,
        postId: Long,
        content: String,
        replyToCommentId: Long? = null
    ): Result<CommunityComment> {
        return try {
            val api = RetrofitClient.createAuthenticatedApiService(token)
            val response = api.createComment(
                postId,
                token,
                CreateCommentRequest(content, replyToCommentId)
            )
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("响应数据为空"))
            } else {
                Result.failure(Exception(response.message() ?: "请求失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 同步数据
     */
    suspend fun syncData(
        token: String,
        lastSyncTime: Long? = null
    ): Result<SyncDataResponse> {
        return try {
            val api = RetrofitClient.createAuthenticatedApiService(token)
            val response = api.syncData(token, lastSyncTime)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("响应数据为空"))
            } else {
                Result.failure(Exception(response.message() ?: "请求失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 上传本地数据
     */
    suspend fun uploadData(
        token: String,
        posts: List<CommunityPost>,
        comments: List<CommunityComment>
    ): Result<Long> {
        return try {
            val api = RetrofitClient.createAuthenticatedApiService(token)
            val response = api.uploadData(token, SyncUploadRequest(posts, comments))
            if (response.isSuccessful) {
                Result.success(response.body()?.syncedAt ?: System.currentTimeMillis())
            } else {
                Result.failure(Exception(response.message() ?: "请求失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}