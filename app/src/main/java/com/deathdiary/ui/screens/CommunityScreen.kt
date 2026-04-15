package com.deathdiary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.deathdiary.data.entities.CommunityPost
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(onNavigateBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    var showNewPostDialog by remember { mutableStateOf(false) }
    val tabs = listOf("推荐", "关注", "热门")

    // Mock 数据
    val posts = remember {
        mutableStateListOf(
            CommunityPost(
                id = 1,
                authorId = 1,
                authorName = "张三",
                authorAvatar = "",
                content = "今天在这里留下了我的第一个脚印，希望这个平台能帮助大家更好地整理自己的数字遗产。",
                timestamp = System.currentTimeMillis() - 3600000,
                likes = 42,
                likedByCurrentUser = false,
                replyCount = 8,
                category = "general",
                tags = "数字遗产,首次"
            ),
            CommunityPost(
                id = 2,
                authorId = 2,
                authorName = "李四",
                authorAvatar = "",
                content = "分享一个使用心得：定期更新遗嘱真的很重要，去年我父亲去世后，幸好他提前准备好了所有信息，省去了很多麻烦。",
                timestamp = System.currentTimeMillis() - 7200000,
                likes = 156,
                likedByCurrentUser = true,
                replyCount = 23,
                isPinned = true,
                category = "sharing",
                tags = "经验分享,遗嘱"
            ),
            CommunityPost(
                id = 3,
                authorName = "王五",
                authorAvatar = "",
                content = "有没有人知道如何安全地保存密码？不想用云服务。",
                timestamp = System.currentTimeMillis() - 86400000,
                likes = 28,
                likedByCurrentUser = false,
                replyCount = 15,
                category = "support",
                tags = "密码管理,安全"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("社区留言板") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showNewPostDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "发帖")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewPostDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "发帖")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab 栏
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // 帖子列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 欢迎卡片
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "欢迎来到社区",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "在这里分享您的数字遗产管理经验，获得帮助和建议。",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // 帖子列表
                items(posts) { post ->
                    CommunityPostCard(
                        post = post,
                        onLike = { 
                            val index = posts.indexOf(post)
                            posts[index] = post.copy(
                                likes = if (post.likedByCurrentUser) post.likes - 1 else post.likes + 1,
                                likedByCurrentUser = !post.likedByCurrentUser
                            )
                        },
                        onComment = { /* TODO: 打开评论 */ }
                    )
                }
            }
        }
    }

    // 新帖对话框
    if (showNewPostDialog) {
        NewPostDialog(
            onDismiss = { showNewPostDialog = false },
            onPost = { content, category, tags ->
                posts.add(
                    0,
                    CommunityPost(
                        id = posts.size + 1L,
                        authorId = 0,
                        authorName = "我",
                        content = content,
                        timestamp = System.currentTimeMillis(),
                        category = category,
                        tags = tags
                    )
                )
                showNewPostDialog = false
            }
        )
    }
}

@Composable
fun CommunityPostCard(
    post: CommunityPost,
    onLike: () -> Unit,
    onComment: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 作者信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 头像
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        if (post.authorAvatar.isNotEmpty()) {
                            // TODO: 加载头像图片
                        } else {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = post.authorName.first().toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatTimestamp(post.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (post.isPinned) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "置顶",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 内容
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium
            )

            // 标签
            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    post.tags.split(",").forEach { tag ->
                        if (tag.trim().isNotEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "#${tag.trim()}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 点赞
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onLike,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (post.likedByCurrentUser) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "点赞",
                            tint = if (post.likedByCurrentUser) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    Text(
                        text = post.likes.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // 评论
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onComment,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "评论"
                        )
                    }
                    Text(
                        text = post.replyCount.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // 分享
                IconButton(
                    onClick = { /* TODO: 分享 */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "分享"
                    )
                }
            }
        }
    }
}

@Composable
fun NewPostDialog(
    onDismiss: () -> Unit,
    onPost: (String, String, String) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("general") }
    var tags by remember { mutableStateOf("") }

    val categories = mapOf(
        "general" to "日常",
        "memorial" to "纪念",
        "sharing" to "经验分享",
        "support" to "求助"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("发布新帖") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("内容") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("分类:", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedCategory == key,
                            onClick = { selectedCategory = key },
                            label = { Text(label) }
                        )
                    }
                }

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("标签（逗号分隔）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("例如: 数字遗产,经验") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onPost(content, selectedCategory, tags) },
                enabled = content.isNotBlank()
            ) {
                Text("发布")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "刚刚"
        diff < 3600000 -> "${diff / 60000}分钟前"
        diff < 86400000 -> "${diff / 3600000}小时前"
        diff < 604800000 -> "${diff / 86400000}天前"
        else -> {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
