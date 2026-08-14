package com.memoamber.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "记忆琥珀",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 欢迎横幅 — 占满两列
            item(span = { GridItemSpan(2) }) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { -it / 3 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
                ) {
                    AmberWelcomeBanner()
                }
            }

            // 功能卡片
            val features = listOf(
                FeatureItem(
                    icon = Icons.Default.Book,
                    title = "日记",
                    subtitle = "记录日常想法",
                    gradient = AmberGradients.diary,
                    iconTint = Color(0xFFD97706),
                    onClick = onNavigateToDiary
                ),
                FeatureItem(
                    icon = Icons.Default.Lock,
                    title = "密码保险箱",
                    subtitle = "安全保存信息",
                    gradient = AmberGradients.vault,
                    iconTint = Color(0xFF6366F1),
                    onClick = onNavigateToVault
                ),
                FeatureItem(
                    icon = Icons.Default.EditNote,
                    title = "未寄出的信",
                    subtitle = "留下最后的嘱托",
                    gradient = AmberGradients.will,
                    iconTint = Color(0xFFEC4899),
                    onClick = onNavigateToWill
                ),
                FeatureItem(
                    icon = Icons.Default.PhotoLibrary,
                    title = "回忆相册",
                    subtitle = "珍藏照片视频",
                    gradient = AmberGradients.gallery,
                    iconTint = Color(0xFF10B981),
                    onClick = onNavigateToGallery
                ),
                FeatureItem(
                    icon = Icons.Default.Forum,
                    title = "社区",
                    subtitle = "分享交流",
                    gradient = AmberGradients.community,
                    iconTint = Color(0xFF3B82F6),
                    onClick = onNavigateToCommunity
                ),
                FeatureItem(
                    icon = Icons.Default.Contacts,
                    title = "关系人",
                    subtitle = "重要的人档案",
                    gradient = AmberGradients.will,
                    iconTint = Color(0xFFF59E0B),
                    onClick = onNavigateToContacts
                ),
                FeatureItem(
                    icon = Icons.Default.Security,
                    title = "安全",
                    subtitle = "隐私保护",
                    gradient = AmberGradients.settings,
                    iconTint = Color(0xFF6B7280),
                    onClick = onNavigateToSettings
                )
            )

            items(features, key = { it.title }) { feature ->
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = features.indexOf(feature) * 80,
                            easing = FastOutSlowInEasing
                        )
                    ) + scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = features.indexOf(feature) * 80,
                            easing = FastOutSlowInEasing
                        )
                    )
                ) {
                    AmberFeatureCard(
                        icon = feature.icon,
                        title = feature.title,
                        subtitle = feature.subtitle,
                        gradient = feature.gradient,
                        iconTint = feature.iconTint,
                        onClick = feature.onClick
                    )
                }
            }

            // 底部间距
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

private data class FeatureItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val gradient: androidx.compose.ui.graphics.Brush,
    val iconTint: Color,
    val onClick: () -> Unit
)
