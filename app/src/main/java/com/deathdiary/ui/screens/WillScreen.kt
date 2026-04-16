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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WillScreen(onNavigateBack: () -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }

    // Mock data
    val wills = remember {
        mutableStateListOf(
            Will(
                id = 1,
                title = "给家人的信",
                content = "亲爱的家人，如果你们看到这封信...",
                recipientName = "家人",
                recipientContact = "family@example.com",
                releaseCondition = "date",
                releaseDate = System.currentTimeMillis() + 31536000000L, // 1 year later
                timestamp = System.currentTimeMillis()
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("遗嘱/遗言") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加遗嘱")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "重要提示",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "这些信息将在您去世后发布给指定联系人。请谨慎填写。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            if (wills.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "还没有创建遗嘱",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(wills) { will ->
                    WillCard(will = will)
                }
            }
        }
    }

    if (showAddDialog) {
        AddWillDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, content, recipientName, recipientContact, releaseCondition, releaseDate ->
                wills.add(
                    Will(
                        id = wills.size + 1L,
                        title = title,
                        content = content,
                        recipientName = recipientName,
                        recipientContact = recipientContact,
                        releaseCondition = releaseCondition,
                        releaseDate = releaseDate,
                        timestamp = System.currentTimeMillis()
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun WillCard(will: Will) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (will.isReleased) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = will.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (will.isReleased) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "已发布",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = will.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "收件人: ${will.recipientName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = will.recipientContact,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                if (will.releaseDate != null) {
                    Text(
                        text = "发布: ${formatDate(will.releaseDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun AddWillDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var recipientContact by remember { mutableStateOf("") }
    var releaseCondition by remember { mutableStateOf("date") }
    var releaseDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("创建遗嘱") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
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
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    label = { Text("收件人姓名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = recipientContact,
                    onValueChange = { recipientContact = it },
                    label = { Text("收件人联系方式") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "发布条件",
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = releaseCondition == "date",
                        onClick = { releaseCondition = "date" },
                        label = { Text("指定日期") }
                    )
                    FilterChip(
                        selected = releaseCondition == "manual",
                        onClick = { releaseCondition = "manual" },
                        label = { Text("手动发布") }
                    )
                }
                if (releaseCondition == "date") {
                    OutlinedTextField(
                        value = releaseDate,
                        onValueChange = { releaseDate = it },
                        label = { Text("发布日期 (YYYY-MM-DD)") },
                        singleLine = true,
                        placeholder = { Text("2026-12-31") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val date = if (releaseCondition == "date" && releaseDate.isNotBlank()) {
                        parseDate(releaseDate)
                    } else null
                    onSave(title, content, recipientName, recipientContact, releaseCondition, date)
                },
                enabled = title.isNotBlank() && content.isNotBlank() &&
                          recipientName.isNotBlank() && recipientContact.isNotBlank()
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




data class Will(
    val id: Long,
    val title: String,
    val content: String,
    val recipientName: String,
    val recipientContact: String,
    val releaseCondition: String,
    val releaseDate: Long?,
    val isReleased: Boolean = false,
    val timestamp: Long
)
