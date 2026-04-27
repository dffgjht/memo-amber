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
fun DiaryScreen(db: DesktopDatabaseManager) {
    var diaries by remember { mutableStateOf(db.getAllDiaries()) }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("日记") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer), actions = { IconButton(onClick = { showDialog = true }) { Icon(Icons.Filled.Add, "新建日记") } }) }
    ) { padding ->
        if (diaries.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                Column(Modifier.align(androidx.compose.ui.Alignment.Center), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Book, null, Modifier.size(64.dp), MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))
                    Text("还没有日记", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("点击右上角 + 开始记录", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(diaries) { diary ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(diary["title"] ?: "", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(diary["content"] ?: "", style = MaterialTheme.typography.bodyMedium, maxLines = 3, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(diary["created_at"] ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                if (diary["mood"]?.isNotEmpty() == true) {
                                    AssistChip(onClick = {}, label = { Text(diary["mood"] ?: "", style = MaterialTheme.typography.bodySmall) }, leadingIcon = { Icon(Icons.Filled.Mood, null, Modifier.size(16.dp)) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false; title = ""; content = ""; mood = "" }, title = { Text("新建日记") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("内容") }, modifier = Modifier.fillMaxWidth().height(150.dp), maxLines = 8)
                OutlinedTextField(value = mood, onValueChange = { mood = it }, label = { Text("心情") }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("开心、平静、忧伤...") })
            }
        }, confirmButton = { Button(onClick = { if (title.isNotBlank()) { db.insertDiary(title, content, mood); diaries = db.getAllDiaries(); showDialog = false; title = ""; content = ""; mood = "" } }, enabled = title.isNotBlank()) { Text("保存") } }, dismissButton = { TextButton(onClick = { showDialog = false }) { Text("取消") } })
    }
}
