package com.memoamber.desktop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.memoamber.desktop.data.DesktopDatabaseManager
import com.memoamber.desktop.data.DesktopSecurityManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WillScreen(db: DesktopDatabaseManager) {
    var wills by remember { mutableStateOf(db.getAllWills()) }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var recipient by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("数字遗嘱") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer), actions = { IconButton(onClick = { showDialog = true }) { Icon(Icons.Filled.Add, "新建遗嘱") } }) }
    ) { padding ->
        if (wills.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                Column(Modifier.align(androidx.compose.ui.Alignment.Center), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.EditNote, null, Modifier.size(64.dp), MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))
                    Text("还没有遗嘱", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("留下你的嘱托和遗言", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(wills) { will ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(will["title"] ?: "", style = MaterialTheme.typography.titleMedium)
                                AssistChip(onClick = {}, label = { Text(will["status"] ?: "草稿", style = MaterialTheme.typography.bodySmall) })
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(will["content"] ?: "", style = MaterialTheme.typography.bodyMedium, maxLines = 5, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (will["recipient"]?.isNotEmpty() == true) {
                                Spacer(Modifier.height(8.dp))
                                Text("收件人: ${will["recipient"]}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, title = { Text("新建遗嘱") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("内容") }, modifier = Modifier.fillMaxWidth().height(200.dp), maxLines = 10)
                OutlinedTextField(value = recipient, onValueChange = { recipient = it }, label = { Text("收件人") }, modifier = Modifier.fillMaxWidth())
            }
        }, confirmButton = { Button(onClick = { if (title.isNotBlank()) { db.insertWill(title, content, recipient); wills = db.getAllWills(); showDialog = false } }, enabled = title.isNotBlank()) { Text("保存") } }, dismissButton = { TextButton(onClick = { showDialog = false }) { Text("取消") } })
    }
}
