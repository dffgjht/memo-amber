package com.memoamber.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════
// 记忆琥珀 — Apple 质感主题
// 画布 #F5F5F7 · 品牌蓝 #0071E3 · 注释灰 #86868B
// ═══════════════════════════════════════════════════════

// ── Light — Apple 浅色 ──────────────────────────────
private val AppleLightPrimary = Color(0xFF0071E3)        // Apple 品牌蓝
private val AppleLightOnPrimary = Color(0xFFFFFFFF)
private val AppleLightPrimaryContainer = Color(0xFFE8F2FF) // 浅蓝
private val AppleLightOnPrimaryContainer = Color(0xFF003A66)
private val AppleLightSecondary = Color(0xFF86868B)      // 注释灰
private val AppleLightOnSecondary = Color(0xFFFFFFFF)
private val AppleLightSecondaryContainer = Color(0xFFE5E5EA) // 次级按钮灰
private val AppleLightOnSecondaryContainer = Color(0xFF1D1D1F)
private val AppleLightTertiary = Color(0xFFB45309)       // 琥珀 — 品牌点缀
private val AppleLightOnTertiary = Color(0xFFFFFFFF)
private val AppleLightTertiaryContainer = Color(0xFFFFF3E0)
private val AppleLightOnTertiaryContainer = Color(0xFF78350F)
private val AppleLightBackground = Color(0xFFF5F5F7)     // Apple 画布
private val AppleLightOnBackground = Color(0xFF1D1D1F)   // 近黑
private val AppleLightSurface = Color(0xFFFFFFFF)
private val AppleLightOnSurface = Color(0xFF1D1D1F)
private val AppleLightSurfaceVariant = Color(0xFFF2F2F7) // 输入框灰
private val AppleLightOnSurfaceVariant = Color(0xFF86868B) // 注释灰
private val AppleLightOutline = Color(0xFFD1D1D6)        // hairline
private val AppleLightError = Color(0xFFFF3B30)          // Apple 红

// ── Dark — Apple 深色 ───────────────────────────────
private val AppleDarkPrimary = Color(0xFF0A84FF)
private val AppleDarkOnPrimary = Color(0xFFFFFFFF)
private val AppleDarkPrimaryContainer = Color(0xFF003A66)
private val AppleDarkOnPrimaryContainer = Color(0xFFA5CFFF)
private val AppleDarkSecondary = Color(0xFF98989D)
private val AppleDarkOnSecondary = Color(0xFF000000)
private val AppleDarkSecondaryContainer = Color(0xFF2C2C2E)
private val AppleDarkOnSecondaryContainer = Color(0xFFE5E5EA)
private val AppleDarkTertiary = Color(0xFFFBBF24)
private val AppleDarkOnTertiary = Color(0xFF1C1917)
private val AppleDarkTertiaryContainer = Color(0xFF92400E)
private val AppleDarkOnTertiaryContainer = Color(0xFFFDE68A)
private val AppleDarkBackground = Color(0xFF000000)
private val AppleDarkOnBackground = Color(0xFFF5F5F7)
private val AppleDarkSurface = Color(0xFF1C1C1E)
private val AppleDarkOnSurface = Color(0xFFF5F5F7)
private val AppleDarkSurfaceVariant = Color(0xFF2C2C2E)
private val AppleDarkOnSurfaceVariant = Color(0xFF98989D)
private val AppleDarkOutline = Color(0xFF48484A)
private val AppleDarkError = Color(0xFFFF453A)
private val AppleDarkOnError = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = AppleLightPrimary,
    onPrimary = AppleLightOnPrimary,
    primaryContainer = AppleLightPrimaryContainer,
    onPrimaryContainer = AppleLightOnPrimaryContainer,
    secondary = AppleLightSecondary,
    onSecondary = AppleLightOnSecondary,
    secondaryContainer = AppleLightSecondaryContainer,
    onSecondaryContainer = AppleLightOnSecondaryContainer,
    tertiary = AppleLightTertiary,
    onTertiary = AppleLightOnTertiary,
    tertiaryContainer = AppleLightTertiaryContainer,
    onTertiaryContainer = AppleLightOnTertiaryContainer,
    background = AppleLightBackground,
    onBackground = AppleLightOnBackground,
    surface = AppleLightSurface,
    onSurface = AppleLightOnSurface,
    surfaceVariant = AppleLightSurfaceVariant,
    onSurfaceVariant = AppleLightOnSurfaceVariant,
    outline = AppleLightOutline,
    error = AppleLightError,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = AppleDarkPrimary,
    onPrimary = AppleDarkOnPrimary,
    primaryContainer = AppleDarkPrimaryContainer,
    onPrimaryContainer = AppleDarkOnPrimaryContainer,
    secondary = AppleDarkSecondary,
    onSecondary = AppleDarkOnSecondary,
    secondaryContainer = AppleDarkSecondaryContainer,
    onSecondaryContainer = AppleDarkOnSecondaryContainer,
    tertiary = AppleDarkTertiary,
    onTertiary = AppleDarkOnTertiary,
    tertiaryContainer = AppleDarkTertiaryContainer,
    onTertiaryContainer = AppleDarkOnTertiaryContainer,
    background = AppleDarkBackground,
    onBackground = AppleDarkOnBackground,
    surface = AppleDarkSurface,
    onSurface = AppleDarkOnSurface,
    surfaceVariant = AppleDarkSurfaceVariant,
    onSurfaceVariant = AppleDarkOnSurfaceVariant,
    outline = AppleDarkOutline,
    error = AppleDarkError,
    onError = AppleDarkOnError,
)

@Composable
fun MemoAmberTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // 关闭动态色，使用品牌蓝
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 状态栏使用画布底色
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            // 导航栏
            window.navigationBarColor = colorScheme.background.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
