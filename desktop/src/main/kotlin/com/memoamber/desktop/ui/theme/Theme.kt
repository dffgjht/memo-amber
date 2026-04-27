package com.memoamber.desktop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 琥珀色系 — 温暖的金黄色调，呼应「记忆琥珀」的意象
private val AmberPrimary = Color(0xFFD4A017)
private val AmberPrimaryContainer = Color(0xFFFFE082)
private val AmberSecondary = Color(0xFF8D6E63)
private val AmberBackground = Color(0xFFFFF8E1)
private val AmberSurface = Color(0xFFFFFBF0)

private val DarkAmberPrimary = Color(0xFFFFB300)
private val DarkAmberPrimaryContainer = Color(0xFF6D4C00)
private val DarkAmberSecondary = Color(0xFFBCAAA4)
private val DarkAmberBackground = Color(0xFF1A1410)
private val DarkAmberSurface = Color(0xFF2A2018)

@Composable
fun MemoAmberTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme(
            primary = DarkAmberPrimary,
            primaryContainer = DarkAmberPrimaryContainer,
            secondary = DarkAmberSecondary,
            background = DarkAmberBackground,
            surface = DarkAmberSurface
        ) else lightColorScheme(
            primary = AmberPrimary,
            primaryContainer = AmberPrimaryContainer,
            secondary = AmberSecondary,
            background = AmberBackground,
            surface = AmberSurface
        ),
        content = content
    )
}
