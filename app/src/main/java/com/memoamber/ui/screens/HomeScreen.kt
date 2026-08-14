package com.memoamber.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memoamber.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDiary: () -> Unit,
    onNavigateToVault: () -> Unit,
    onNavigateToWill: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    // 入场动画
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Aurora 极光弥散背景 + Bento 内容
    AuroraBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "记忆琥珀",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        // 设置 — 浅灰圆形胶囊
                        IconButton(onClick = onNavigateToSettings) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "设置",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Bento Grid — 2 列，宽敞间距 20dp，四周 24dp
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(start = 24.dp, top = 4.dp, end = 24.dp, bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Hero 大卡 — 占满两列
                item(span = { GridItemSpan(2) }) {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeInUp(0)
                    ) {
                        AppleHeroBanner(
                            title = "记忆琥珀",
                            subtitle = "珍藏回忆，留住美好",
                            actionText = "写一篇日记",
                            onAction = onNavigateToDiary
                        )
                    }
                }

                // Bento 功能卡片
                val features = listOf(
                    AppleFeature(
                        icon = Icons.Default.Book,
                        title = "日记",
                        subtitle = "记录日常想法",
                        tint = Color(0xFFD97706),
                        onClick = onNavigateToDiary
                    ),
                    AppleFeature(
                        icon = Icons.Default.Lock,
                        title = "密码保险箱",
                        subtitle = "安全保存信息",
                        tint = Color(0xFF6366F1),
                        onClick = onNavigateToVault
                    ),
                    AppleFeature(
                        icon = Icons.Default.EditNote,
                        title = "未寄出的信",
                        subtitle = "留下最后的嘱托",
                        tint = Color(0xFFEC4899),
                        onClick = onNavigateToWill
                    ),
                    AppleFeature(
                        icon = Icons.Default.PhotoLibrary,
                        title = "回忆相册",
                        subtitle = "珍藏照片视频",
                        tint = Color(0xFF10B981),
                        onClick = onNavigateToGallery
                    ),
                    AppleFeature(
                        icon = Icons.Default.Forum,
                        title = "社区",
                        subtitle = "分享交流",
                        tint = Color(0xFF3B82F6),
                        onClick = onNavigateToCommunity
                    ),
                    AppleFeature(
                        icon = Icons.Default.Contacts,
                        title = "关系人",
                        subtitle = "重要的人档案",
                        tint = Color(0xFFF59E0B),
                        onClick = onNavigateToContacts
                    )
                )

                // 序列入场 — 每张卡依次 fade-in-up
                itemsIndexed(features, key = { _, f -> f.title }) { index, feature ->
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeInUp(delayMillis = 140 + index * 90)
                    ) {
                        AppleFeatureCard(
                            icon = feature.icon,
                            title = feature.title,
                            subtitle = feature.subtitle,
                            tint = feature.tint,
                            onClick = feature.onClick
                        )
                    }
                }
            }
        }
    }
}

private data class AppleFeature(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val tint: Color,
    val onClick: () -> Unit
)
