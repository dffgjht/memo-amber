package com.memoamber.desktop

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.memoamber.desktop.ui.screens.*
import com.memoamber.desktop.ui.theme.MemoAmberTheme
import com.memoamber.desktop.data.DesktopSecurityManager
import com.memoamber.desktop.data.DesktopDatabaseManager

@Composable
fun MemoAmberApp() {
    val securityManager = remember { DesktopSecurityManager() }
    val databaseManager = remember { DesktopDatabaseManager() }
    val currentRoute = remember { mutableStateOf("lock") }
    val sessionPassword = remember { mutableStateOf("") }

    MemoAmberTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            AnimatedContent(
                targetState = currentRoute.value,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) }
            ) { route ->
                when (route) {
                    "lock" -> LockScreen(
                        securityManager = securityManager,
                        onAuthSuccess = { pwd ->
                            sessionPassword.value = pwd
                            currentRoute.value = "home"
                        }
                    )
                    "home" -> MainScreen(
                        securityManager = securityManager,
                        databaseManager = databaseManager,
                        dek = remember(sessionPassword.value) {
                            if (sessionPassword.value.isNotEmpty()) securityManager.getDataEncryptionKey(sessionPassword.value) else ByteArray(0)
                        },
                        onLock = {
                            sessionPassword.value = ""
                            currentRoute.value = "lock"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    securityManager: DesktopSecurityManager,
    databaseManager: DesktopDatabaseManager,
    dek: ByteArray,
    onLock: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Spacer(Modifier.weight(0.5f))

            val tabs = listOf(
                "主页" to Icons.Filled.Home,
                "日记" to Icons.Filled.Book,
                "密码箱" to Icons.Filled.Lock,
                "遗嘱" to Icons.Filled.EditNote,
                "设置" to Icons.Filled.Settings
            )

            tabs.forEachIndexed { index, (label, icon) ->
                NavigationRailItem(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    icon = { Icon(icon, label) },
                    label = { Text(label, maxLines = 1) }
                )
            }

            Spacer(Modifier.weight(1f))

            NavigationRailItem(
                selected = false,
                onClick = onLock,
                icon = { Icon(Icons.Filled.Lock, "锁定") },
                label = { Text("锁定", maxLines = 1) }
            )
            Spacer(Modifier.height(16.dp))
        }

        Divider(
            modifier = Modifier.fillMaxHeight().width(1.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> DiaryScreen(databaseManager)
                2 -> VaultScreen(databaseManager, securityManager, dek)
                3 -> WillScreen(databaseManager)
                4 -> SettingsScreen(securityManager, databaseManager, onLock)
            }
        }
    }
}
