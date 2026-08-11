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
// 记忆琥珀 — 琥珀暖色调主题
// ═══════════════════════════════════════════════════════

// Light Theme — 温暖琥珀色
private val AmberLightPrimary = Color(0xFFB45309)        // amber-700
private val AmberLightOnPrimary = Color(0xFFFFFFFF)
private val AmberLightPrimaryContainer = Color(0xFFFFF3E0) // amber-50
private val AmberLightOnPrimaryContainer = Color(0xFF78350F) // amber-900
private val AmberLightSecondary = Color(0xFFD97706)      // amber-600
private val AmberLightOnSecondary = Color(0xFFFFFFFF)
private val AmberLightSecondaryContainer = Color(0xFFFFECB3) // amber-100
private val AmberLightOnSecondaryContainer = Color(0xFF78350F)
private val AmberLightTertiary = Color(0xFF92400E)       // amber-800
private val AmberLightOnTertiary = Color(0xFFFFFFFF)
private val AmberLightTertiaryContainer = Color(0xFFFDE68A) // amber-200
private val AmberLightOnTertiaryContainer = Color(0xFF78350F)
private val AmberLightBackground = Color(0xFFFFFBF5)     // warm white
private val AmberLightOnBackground = Color(0xFF1C1917)   // stone-900
private val AmberLightSurface = Color(0xFFFFFFFF)
private val AmberLightOnSurface = Color(0xFF1C1917)
private val AmberLightSurfaceVariant = Color(0xFFF5F0EB)
private val AmberLightOnSurfaceVariant = Color(0xFF57534E) // stone-600
private val AmberLightOutline = Color(0xFFD6D3D1)        // stone-300
private val AmberLightError = Color(0xFFDC2626)          // red-600
private val AmberLightOnError = Color(0xFFFFFFFF)

// Dark Theme — 深色琥珀
private val AmberDarkPrimary = Color(0xFFFCD34D)         // amber-300
private val AmberDarkOnPrimary = Color(0xFF78350F)       // amber-900
private val AmberDarkPrimaryContainer = Color(0xFF92400E) // amber-800
private val AmberDarkOnPrimaryContainer = Color(0xFFFDE68A) // amber-200
private val AmberDarkSecondary = Color(0xFFFBBF24)       // amber-400
private val AmberDarkOnSecondary = Color(0xFF78350F)
private val AmberDarkSecondaryContainer = Color(0xFFB45309) // amber-700
private val AmberDarkOnSecondaryContainer = Color(0xFFFDE68A)
private val AmberDarkTertiary = Color(0xFFF59E0B)        // amber-500
private val AmberDarkOnTertiary = Color(0xFF78350F)
private val AmberDarkTertiaryContainer = Color(0xFFD97706) // amber-600
private val AmberDarkOnTertiaryContainer = Color(0xFFFFFBEB) // amber-50
private val AmberDarkBackground = Color(0xFF1C1917)      // stone-900
private val AmberDarkOnBackground = Color(0xFFF5F5F4)    // stone-100
private val AmberDarkSurface = Color(0xFF292524)         // stone-800
private val AmberDarkOnSurface = Color(0xFFF5F5F4)
private val AmberDarkSurfaceVariant = Color(0xFF44403C)  // stone-700
private val AmberDarkOnSurfaceVariant = Color(0xFFA8A29E) // stone-400
private val AmberDarkOutline = Color(0xFF78716C)         // stone-500
private val AmberDarkError = Color(0xFFFCA5A5)           // red-300
private val AmberDarkOnError = Color(0xFF7F1D1D)         // red-900

private val LightColorScheme = lightColorScheme(
    primary = AmberLightPrimary,
    onPrimary = AmberLightOnPrimary,
    primaryContainer = AmberLightPrimaryContainer,
    onPrimaryContainer = AmberLightOnPrimaryContainer,
    secondary = AmberLightSecondary,
    onSecondary = AmberLightOnSecondary,
    secondaryContainer = AmberLightSecondaryContainer,
    onSecondaryContainer = AmberLightOnSecondaryContainer,
    tertiary = AmberLightTertiary,
    onTertiary = AmberLightOnTertiary,
    tertiaryContainer = AmberLightTertiaryContainer,
    onTertiaryContainer = AmberLightOnTertiaryContainer,
    background = AmberLightBackground,
    onBackground = AmberLightOnBackground,
    surface = AmberLightSurface,
    onSurface = AmberLightOnSurface,
    surfaceVariant = AmberLightSurfaceVariant,
    onSurfaceVariant = AmberLightOnSurfaceVariant,
    outline = AmberLightOutline,
    error = AmberLightError,
    onError = AmberLightOnError,
)

private val DarkColorScheme = darkColorScheme(
    primary = AmberDarkPrimary,
    onPrimary = AmberDarkOnPrimary,
    primaryContainer = AmberDarkPrimaryContainer,
    onPrimaryContainer = AmberDarkOnPrimaryContainer,
    secondary = AmberDarkSecondary,
    onSecondary = AmberDarkOnSecondary,
    secondaryContainer = AmberDarkSecondaryContainer,
    onSecondaryContainer = AmberDarkOnSecondaryContainer,
    tertiary = AmberDarkTertiary,
    onTertiary = AmberDarkOnTertiary,
    tertiaryContainer = AmberDarkTertiaryContainer,
    onTertiaryContainer = AmberDarkOnTertiaryContainer,
    background = AmberDarkBackground,
    onBackground = AmberDarkOnBackground,
    surface = AmberDarkSurface,
    onSurface = AmberDarkOnSurface,
    surfaceVariant = AmberDarkSurfaceVariant,
    onSurfaceVariant = AmberDarkOnSurfaceVariant,
    outline = AmberDarkOutline,
    error = AmberDarkError,
    onError = AmberDarkOnError,
)

@Composable
fun MemoAmberTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // 关闭动态色，使用品牌琥珀色
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
            // 状态栏使用半透明琥珀色
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
