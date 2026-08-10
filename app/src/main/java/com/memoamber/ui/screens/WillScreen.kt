package com.memoamber.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.memoamber.data.MemoAmberDatabase
import com.memoamber.data.entities.Will
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WillScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var showAddDialog by remember { mutableStateOf(false) }
    
    // 使用 MutableStateFlow 管理数据
    val willsFlow = remember { MutableStateFlow<List<Will>>(emptyList()) }
    val wills by willsFlow.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    // 加载数据函数
    fun loadWills() {
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val database = MemoAmberDatabase.getDatabase(context)
                    val dao = database.willDao()
                    val allWills = dao.getAllWillsSync()
                    willsFlow.value = allWills
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isLoading = false
                }
            }
        }
    }

    // 首次加载
    LaunchedEffect(Unit) {
        loadWills()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("遗嘱 / 遗言", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加遗嘱")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Edit, contentDescription = "添加遗嘱")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (wills.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("还没有创建遗嘱", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("点击 + 写下对家人的嘱托", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(wills, key = { it.id }) { will ->
                    WillCard(will = will)
                }
            }
        }
    }

    if (showAddDialog) {
        AddWillFullDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, content, recipientName, recipientContact, releaseCondition, releaseDate ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val database = MemoAmberDatabase.getDatabase(context)
                            val dao = database.willDao()
                            
                            dao.insertWill(
                                Will(
                                    title = title,
                                    content = content,
                                    recipientName = recipientName,
                                    recipientContact = recipientContact,
                                    releaseCondition = releaseCondition,
                                    releaseDate = releaseDate,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            loadWills() // 刷新数据
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun WillCard(will: Will) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = will.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                
                // 发送按钮
                if (will.recipientContact.isNotBlank()) {
                    IconButton(
                        onClick = {
                            val isEmail = will.recipientContact.contains("@")
                            val intent = if (isEmail) {
                                Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${will.recipientContact}")
                                    putExtra(Intent.EXTRA_SUBJECT, will.title)
                                    putExtra(Intent.EXTRA_TEXT, will.content)
                                }
                            } else {
                                Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("smsto:${will.recipientContact}")
                                    putExtra("sms_body", will.content)
                                }
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "发送",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = will.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📩 收件人：${will.recipientName} (${will.recipientContact})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (will.releaseDate != null) {
                Text(
                    text = "📅 发布日期：${formatDateTimeFull(will.releaseDate!!)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AddWillFullDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var recipientContact by remember { mutableStateOf("") }
    var releaseCondition by remember { mutableStateOf("date") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Text("创建遗嘱",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题 *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("遗言内容 *") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    minLines = 6
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    label = { Text("收件人姓名 *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = recipientContact,
                    onValueChange = { recipientContact = it },
                    label = { Text("联系方式 *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank() && 
                                recipientName.isNotBlank() && recipientContact.isNotBlank()) {
                                onSave(title, content, recipientName, recipientContact, 
                                    releaseCondition, null)
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank() && 
                                recipientName.isNotBlank() && recipientContact.isNotBlank()
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}
