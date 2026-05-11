package com.deathdiary.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deathdiary.data.entities.CommunityPost
import com.deathdiary.network.DataRepository
import com.deathdiary.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 社区 ViewModel
 */
class CommunityViewModel : ViewModel() {
    
    private val repository = DataRepository(RetrofitClient.apiService)
    
    // 帖子列表
    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()
    
    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // 登录状态
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    // 错误信息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // 用户 Token
    private var userToken: String? = null
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        _isLoggedIn.value = false
    }
    
    fun loadPosts(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.getPosts(category = category)
                .onSuccess { postsList ->
                    _posts.value = postsList
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "加载失败"
                }
            
            _isLoading.value = false
        }
    }
    
    fun createPost(content: String, category: String, tags: String) {
        if (userToken == null) {
            _errorMessage.value = "请先登录"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.createPost(userToken!!, content, category, tags)
                .onSuccess { newPost ->
                    val updatedPosts = _posts.value.toMutableList()
                    updatedPosts.add(0, newPost)
                    _posts.value = updatedPosts
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "发布失败"
                }
            
            _isLoading.value = false
        }
    }
    
    fun likePost(postId: Long) {
        if (userToken == null) {
            _errorMessage.value = "请先登录"
            return
        }
        
        viewModelScope.launch {
            val post = _posts.value.find { it.id == postId }
            if (post != null) {
                if (post.likedByCurrentUser) {
                    repository.unlikePost(userToken!!, postId)
                        .onSuccess { likes ->
                            updatePostLike(postId, likes, false)
                        }
                } else {
                    repository.likePost(userToken!!, postId)
                        .onSuccess { likes ->
                            updatePostLike(postId, likes, true)
                        }
                }
            }
        }
    }
    
    private fun updatePostLike(postId: Long, likes: Int, liked: Boolean) {
        val updatedPosts = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(likes = likes, likedByCurrentUser = liked)
            } else {
                post
            }
        }
        _posts.value = updatedPosts
    }
}
