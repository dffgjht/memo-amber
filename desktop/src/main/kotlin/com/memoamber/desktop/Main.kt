package com.memoamber.desktop

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "记忆琥珀 - MemoAmber",
        state = rememberWindowState(width = 1200.dp, height = 800.dp)
    ) {
        window.minimumSize = Dimension(900, 600)
        MemoAmberApp()
    }
}
