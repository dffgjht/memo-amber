package com.memoamber.desktop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.memoamber.desktop.data.DesktopDatabaseManager
import com.memoamber.desktop.data.DesktopSecurityManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(db: DesktopDatabaseManager, security: DesktopSecurityManager, dek: ByteArray) {
    var items by remember { mutableStateOf(db.getAllVaultItems()) }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("密码保险箱") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer), actions = { IconButton(onClick = { showDialog = true }) { Icon(Icons.Filled.Add, "添加") } }) }
    ) { padding ->
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                Column(Modifier.align(androidx.compose.ui.Alignment.Center), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Lock, null, Modifier.size(64.dp), MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))
                    Text("密码箱为空", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items) { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(item["title"] ?: "", style = MaterialTheme.typography.titleMedium)
                                Text(item["username"] ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (item["url"]?.isNotEmpty() == true) Text(item["url"] ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = {
                                item["id"]?.toLongOrNull()?.let { db.deleteVaultItem(it); items = db.getAllVaultItems() }
                            }) { Icon(Icons.Filled.Delete, "删除", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, title = { Text("添加密码") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("用户名") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("密码") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("网址") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("备注") }, modifier = Modifier.fillMaxWidth())
            }
        }, confirmButton = { Button(onClick = {
            val encPwd = security.encryptString(password, dek)
            db.insertVaultItem(title, username, encPwd, url, notes)
            items = db.getAllVaultItems(); showDialog = false
        }, enabled = title.isNotBlank() && password.isNotBlank()) { Text("保存") } }, dismissButton = { TextButton(onClick = { showDialog = false }) { Text("取消") } })
    }
}
