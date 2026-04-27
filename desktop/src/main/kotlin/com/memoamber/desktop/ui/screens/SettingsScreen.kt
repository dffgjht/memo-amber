package com.memoamber.desktop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.memoamber.desktop.data.DesktopDatabaseManager
import com.memoamber.desktop.data.DesktopSecurityManager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(security: DesktopSecurityManager, db: DesktopDatabaseManager, onLock: () -> Unit) {
    val dataDir = File(System.getProperty("user.home"), ".memoamber")
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("设置") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // 数据目录信息
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("数据存储", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("数据目录: ${dataDir.absolutePath}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("数据库: ${File(dataDir, "data.db").absolutePath}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // 安全设置
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("安全", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { showResetDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Key, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("重置主密码")
                    }
                }
            }

            // 锁定
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("会话", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onLock, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Lock, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("立即锁定")
                    }
                }
            }

            // 关于
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("关于", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("记忆琥珀 MemoAmber v1.4.0", style = MaterialTheme.typography.bodyMedium)
                    Text("琥珀封存记忆，守护你的珍贵时光", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(onDismissRequest = { showResetDialog = false }, title = { Text("重置主密码") }, text = { Text("此操作将清除所有加密数据并重置密码。确定继续吗？") }, confirmButton = { Button(onClick = { showResetDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("确认重置") } }, dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("取消") } })
    }
}
