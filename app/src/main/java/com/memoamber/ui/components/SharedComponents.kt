package com.memoamber.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memoamber.ui.theme.*
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════
// 琥珀渐变色
// ═══════════════════════════════════════════════════════
object AmberGradients {
    // 主渐变 — 从深琥珀到浅琥珀
    val primary = Brush.linearGradient(
        colors = listOf(
            Color(0xFFB45309), // amber-700
            Color(0xFFD97706), // amber-600
            Color(0xFFF59E0B)  // amber-500
        )
    )

    // 浅渐变 — 用于卡片背景
    val surface = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFF8F0), // warm white
            Color(0xFFFFF3E0)  // amber-50
        )
    )

    // 深色渐变 — 用于锁屏等
    val dark = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF78350F), // amber-900
            Color(0xFF92400E), // amber-800
            Color(0xFFB45309)  // amber-700
        )
    )

    // 夜间深色渐变
    val darkNight = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1C1917), // stone-900
            Color(0xFF292524), // stone-800
            Color(0xFF44403C)  // stone-700
        )
    )

    // 卡片渐变 — 不同功能不同颜色
    val diary = Brush.linearGradient(
        colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A)) // amber-100 → amber-200
    )
    val vault = Brush.linearGradient(
        colors = listOf(Color(0xFFE0E7FF), Color(0xFFC7D2FE)) // indigo-100 → indigo-200
    )
    val will = Brush.linearGradient(
        colors = listOf(Color(0xFFFCE7F3), Color(0xFFFBCFE8)) // pink-100 → pink-200
    )
    val gallery = Brush.linearGradient(
        colors = listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0)) // emerald-100 → emerald-200
    )
    val community = Brush.linearGradient(
        colors = listOf(Color(0xFFDBEAFE), Color(0xFFBFDBFE)) // blue-100 → blue-200
    )
    val settings = Brush.linearGradient(
        colors = listOf(Color(0xFFF3F4F6), Color(0xFFE5E7EB)) // gray-100 → gray-200
    )
}

// ═══════════════════════════════════════════════════════
// 琥珀主题功能卡片 — 带渐变背景和图标
// ═══════════════════════════════════════════════════════
@Composable
fun AmberFeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradient: Brush,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = iconTint.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(20.dp)
        ) {
            Column {
                // 图标容器
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconTint.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════
// 琥珀欢迎横幅 — 带渐变背景
// ═══════════════════════════════════════════════════════
@Composable
fun AmberWelcomeBanner(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "banner")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFB45309).copy(alpha = 0.9f + shimmer * 0.1f),
                            Color(0xFFD97706),
                            Color(0xFFF59E0B).copy(alpha = 0.8f + shimmer * 0.2f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "✨ 记忆琥珀",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "珍藏回忆，留住美好",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════
// 琥珀锁屏 — 渐变背景锁屏
// ═══════════════════════════════════════════════════════
@Composable
fun AmberLockBackground(
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    val gradient = if (isDark) AmberGradients.darkNight else AmberGradients.dark

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// ═══════════════════════════════════════════════════════
// 通用左滑删除容器 — 列表项左滑显示删除背景并触发 onDelete
// ═══════════════════════════════════════════════════════
@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val thresholdPx = with(density) { 140.dp.toPx() }
    val maxSwipePx = with(density) { 240.dp.toPx() }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        // 删除背景（左滑时露出）
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                modifier = Modifier.padding(end = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "删除",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        // 前景卡片（跟随手指滑动）
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value <= -thresholdPx) {
                                    onDelete()
                                    offsetX.snapTo(0f)
                                } else {
                                    offsetX.animateTo(0f, animationSpec = tween(220))
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                offsetX.snapTo((offsetX.value + dragAmount).coerceIn(-maxSwipePx, 0f))
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}

// ═══════════════════════════════════════════════════════
// 区域标题
// ═══════════════════════════════════════════════════════
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = 4.dp)
    )
}

// ═══════════════════════════════════════════════════════
// 琥珀按钮 — 带渐变背景
// ═══════════════════════════════════════════════════════
@Composable
fun AmberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            disabledContentColor = MaterialTheme.colorScheme.outline
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ═══════════════════════════════════════════════════════
// 琥珀输入框 — 带圆角和填充背景
// ═══════════════════════════════════════════════════════
@Composable
fun AmberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = if (leadingIcon != null) {
            { Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        } else null,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        singleLine = true
    )
}

// ═══════════════════════════════════════════════════════
// ===== Apple 质感组件库 =====
// Bento Grid · Aurora & Glass · Big Type · Physics
// ═══════════════════════════════════════════════════════

// 弹簧曲线 — 等效 cubic-bezier(0.25, 1, 0.5, 1)
val AppleSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

// ── 入场动画 — fade-in-up（Apple 序列入场）────────────────
fun fadeInUp(
    delayMillis: Int = 0,
    durationMillis: Int = 550
): EnterTransition = fadeIn(
    animationSpec = tween(durationMillis, delayMillis, easing = FastOutSlowInEasing)
) + slideInVertically(
    animationSpec = tween(durationMillis, delayMillis, easing = FastOutSlowInEasing)
) { it / 4 }

// ── 毛玻璃底色 — 半透明白 / 深色半透明 ───────────────────
val GlassColor: Color
    @Composable get() = if (isSystemInDarkTheme()) {
        Color(0xFF1C1C1E).copy(alpha = 0.72f)
    } else {
        Color.White.copy(alpha = 0.78f)
    }

// Hairline — 1px 半透明边框色
val HairlineColor: Color
    @Composable get() = if (isSystemInDarkTheme()) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.White.copy(alpha = 0.65f)
    }

// ── Aurora 极光弥散背景 — 基色 + 光球 + 缓慢浮动 ──────────
@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "aurora")
    val drift1 by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift1"
    )
    val drift2 by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(13000, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 光球 1 — 淡蓝
        AuroraOrb(
            size = 560.dp, color = Color(0xFFA2D2FF), alpha = 0.45f,
            offsetX = (-40).dp, offsetY = (-80).dp, drift = drift1, driftAmplitude = 90.dp
        )
        // 光球 2 — 淡紫
        AuroraOrb(
            size = 620.dp, color = Color(0xFFC8B4FF), alpha = 0.32f,
            offsetX = 90.dp, offsetY = 60.dp, drift = drift2, driftAmplitude = 110.dp
        )
        // 光球 3 — 琥珀（品牌点缀）
        AuroraOrb(
            size = 520.dp, color = Color(0xFFFFC98A), alpha = 0.26f,
            offsetX = (-20).dp, offsetY = 160.dp, drift = drift2, driftAmplitude = 80.dp
        )
        content()
    }
}

@Composable
private fun BoxScope.AuroraOrb(
    size: Dp,
    color: Color,
    alpha: Float,
    offsetX: Dp,
    offsetY: Dp,
    drift: Float,
    driftAmplitude: Dp
) {
    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(
                x = offsetX + driftAmplitude * (2f * (drift - 0.5f)),
                y = offsetY + driftAmplitude * (1.6f * (drift - 0.5f))
            )
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(color.copy(alpha = alpha), color.copy(alpha = 0f))
                )
            )
            .blur(80.dp) // API 31+ 真模糊，低版本自动降级为柔和渐变
    )
}

// ── Bento 功能卡片 — 玻璃质感 + 按压物理反馈 ──────────────
@Composable
fun AppleFeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = AppleSpring,
        label = "cardScale"
    )
    val shape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (pressed) 4.dp else 10.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.16f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(GlassColor)
            .border(1.dp, HairlineColor, shape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 图标容器 — 微渐变背景
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(tint.copy(alpha = 0.22f), tint.copy(alpha = 0.08f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Hero 大卡 — 玻璃 + 右上角品牌光晕 ────────────────────
@Composable
fun AppleHeroBanner(
    title: String,
    subtitle: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(32.dp)
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.18f),
                spotColor = Color.Black.copy(alpha = 0.14f)
            )
            .clip(shape)
            .background(GlassColor)
            .border(1.dp, HairlineColor, shape)
            .padding(24.dp)
    ) {
        // 右上角品牌光晕
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(primary.copy(alpha = 0.20f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            AppleButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.width(168.dp)
            )
        }
    }
}

// ── 主按钮 — 品牌蓝实心 + 胶囊 + 按压缩放 ─────────────────
@Composable
fun AppleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = AppleSpring,
        label = "buttonScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        interactionSource = interaction,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── 次级按钮 — 浅灰实心填充（禁止透明）───────────────────
@Composable
fun AppleSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = AppleSpring,
        label = "secButtonScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        interactionSource = interaction,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── 输入框 — 灰底无边框，Focus 白底 + 品牌蓝光晕 ──────────
@Composable
fun AppleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        focused -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    val containerColor = if (focused) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val glow = if (focused) {
        Modifier.shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(16.dp),
            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        )
    } else {
        Modifier
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = if (leadingIcon != null) {
            { Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        } else null,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        interactionSource = interaction,
        modifier = modifier
            .fillMaxWidth()
            .then(glow),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            errorLabelColor = MaterialTheme.colorScheme.error
        ),
        singleLine = singleLine,
        minLines = minLines
    )
}
