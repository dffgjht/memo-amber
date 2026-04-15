package com.deathdiary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(onNavigateBack: () -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    // Mock data - replace with actual database queries
    val entries = remember {
        mutableStateListOf(
            DiaryEntry(
                id = 1,
                title = "美好的一天",
                content = "今天天气很好，和朋友们度过了愉快的时光...",
                mood = "happy",
                timestamp = System.currentTimeMillis() - 86400000
            ),
            DiaryEntry(
                id = 2,
                title = "思考人生",
                content = "最近总是在思考生命的意义...",
                mood = "neutral",
                timestamp = System.currentTimeMillis() - 172800000
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日记") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加日记")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (entries.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "还没有日记",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "点击右上角 + 添加第一条日记",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(entries) { entry ->
                    DiaryEntryCard(
                        entry = entry,
                        onClick = { selectedEntry = entry }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddDiaryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, content, mood ->
                entries.add(
                    DiaryEntry(
                        id = entries.size + 1,
                        title = title,
                        content = content,
                        mood = mood,
                        timestamp = System.currentTimeMillis()
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DiaryEntryCard(entry: DiaryEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTimestamp(entry.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                MoodBadge(mood = entry.mood)
            }
        }
    }
}

@Composable
fun MoodBadge(mood: String) {
    val (emoji, label) = when (mood) {
        "happy" -> "😊" to "开心"
        "sad" -> "😢" to "难过"
        "neutral" -> "😐" to "平静"
        "excited" -> "🤩" to "兴奋"
        "anxious" -> "😰" to "焦虑"
        else -> "😐" to "平静"
    }

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "$emoji $label",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun AddDiaryDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("neutral") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新日记") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("内容") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("心情:", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MoodOption("happy", "😊", mood) { mood = it }
                    MoodOption("sad", "😢", mood) { mood = it }
                    MoodOption("neutral", "😐", mood) { mood = it }
                    MoodOption("excited", "🤩", mood) { mood = it }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, content, mood) },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun MoodOption(value: String, emoji: String, selected: String, onSelect: (String) -> Unit) {
    FilterChip(
        selected = selected == value,
        onClick = { onSelect(value) },
        label = { Text(emoji) }
    )
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

data class DiaryEntry(
    val id: Long,
    val title: String,
    val content: String,
    val mood: String,
    val timestamp: Long
)
