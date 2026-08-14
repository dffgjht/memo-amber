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
import com.memoamber.data.entities.Contact
import com.memoamber.data.entities.Will
import com.memoamber.ui.components.SwipeToDeleteContainer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WillScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingWill by remember { mutableStateOf<Will?>(null) }
    var pendingDelete by remember { mutableStateOf<Will?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    
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
                        Text("未寄出的信", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { editingWill = null; showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "写一封信")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editingWill = null; showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Edit, contentDescription = "写一封信")
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
                    Text("还没有写下的信", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("点击 + 写下想说却未说出口的话", style = MaterialTheme.typography.bodyMedium,
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
                    SwipeToDeleteContainer(
                        onDelete = { pendingDelete = will }
                    ) {
                        WillCard(
                            will = will,
                            onClick = { editingWill = will; showAddDialog = true },
                            onEdit = { editingWill = will; showAddDialog = true },
                            onDelete = { pendingDelete = will }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddWillFullDialog(
            initial = editingWill,
            onDismiss = { showAddDialog = false; editingWill = null },
            onSave = { title, content, recipientName, recipientContact, releaseCondition, releaseDate ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val database = MemoAmberDatabase.getDatabase(context)
                            val dao = database.willDao()
                            
                            val will = Will(
                                id = editingWill?.id ?: 0,
                                title = title,
                                content = content,
                                recipientName = recipientName,
                                recipientContact = recipientContact,
                                contactType = if (recipientContact.contains("@")) "email" else "phone",
                                releaseCondition = releaseCondition,
                                releaseDate = releaseDate,
                                timestamp = editingWill?.timestamp ?: System.currentTimeMillis()
                            )
                            
                            if (editingWill != null) dao.updateWill(will) else dao.insertWill(will)
                            loadWills() // 刷新数据
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    val isEditing = editingWill != null
                    showAddDialog = false
                    editingWill = null
                    snackbarHostState.showSnackbar(if (isEditing) "信已修改" else "信已写好")
                }
            }
        )
    }

    // 删除确认
    pendingDelete?.let { will ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("删除信件") },
            text = { Text("确定要删除「${will.title}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            try { MemoAmberDatabase.getDatabase(context).willDao().deleteWillById(will.id) }
                            catch (e: Exception) { e.printStackTrace() }
                        }
                        loadWills()
                        pendingDelete = null
                        snackbarHostState.showSnackbar("信件已删除")
                    }
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("取消") } }
        )
    }
}

@Composable
fun WillCard(
    will: Will,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
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

                // 更多操作菜单
                Box {
                    IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "更多操作",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("✏️ 编辑") },
                            onClick = { menuExpanded = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text("🗑️ 删除", color = MaterialTheme.colorScheme.error) },
                            onClick = { menuExpanded = false; onDelete() }
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
    initial: Will? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, Long?) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var content by remember { mutableStateOf(initial?.content ?: "") }
    var recipientName by remember { mutableStateOf(initial?.recipientName ?: "") }
    var recipientContact by remember { mutableStateOf(initial?.recipientContact ?: "") }
    var releaseCondition by remember { mutableStateOf("date") }
    var showContactPicker by remember { mutableStateOf(false) }
    val contacts = remember { mutableStateOf<List<Contact>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                contacts.value = MemoAmberDatabase.getDatabase(context).contactDao().getAllContactsSync()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(if (initial == null) "写一封信" else "修改信件",
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
                    label = { Text("信件内容 *") },
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
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = recipientContact,
                    onValueChange = { recipientContact = it },
                    label = { Text("联系方式 *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 从关系人选择
                OutlinedButton(
                    onClick = { showContactPicker = true },
                    enabled = contacts.value.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Contacts, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (contacts.value.isEmpty()) "暂无关系人档案，可先去「关系人」添加" else "从关系人选择")
                }
                
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

    // 关系人选择对话框
    if (showContactPicker) {
        AlertDialog(
            onDismissRequest = { showContactPicker = false },
            title = { Text("选择关系人作为收件人") },
            text = {
                if (contacts.value.isEmpty()) {
                    Text("暂无关系人，请先在「关系人」中添加")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(contacts.value, key = { it.id }) { contact ->
                            Card(
                                onClick = {
                                    recipientName = contact.name
                                    recipientContact = if (contact.phone.isNotBlank()) contact.phone else contact.email
                                    showContactPicker = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = contact.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = contact.relationship.ifBlank { "—" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showContactPicker = false }) { Text("关闭") }
            }
        )
    }
}
