package com.memoamber.ui.screens

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
import androidx.compose.ui.window.DialogProperties
import com.memoamber.data.MemoAmberDatabase
import com.memoamber.data.entities.VaultItem
import com.memoamber.ui.components.SwipeToDeleteContainer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var editingItem by remember { mutableStateOf<VaultItem?>(null) }
    var pendingDelete by remember { mutableStateOf<VaultItem?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // 使用 MutableStateFlow 管理数据
    val itemsFlow = remember { MutableStateFlow<List<VaultItem>>(emptyList()) }
    val items by itemsFlow.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    // 加载数据函数
    fun loadItems() {
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val database = MemoAmberDatabase.getDatabase(context)
                    val dao = database.vaultItemDao()
                    val allItems = dao.getAllItemsSync()
                    itemsFlow.value = allItems
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
        loadItems()
    }

    // 刷新数据
    LaunchedEffect(onNavigateBack) {
        loadItems()
    }

    val filteredItems = remember(searchQuery, items) {
        if (searchQuery.isBlank()) items
        else items.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("密码保险箱", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加项目")
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
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("搜索保险箱项目...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("保险箱是空的", style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("点击 + 添加您的第一个密码", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        SwipeToDeleteContainer(
                            onDelete = { pendingDelete = item }
                        ) {
                            VaultItemCard(item = item, onClick = { editingItem = item })
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddVaultItemFullDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, category, content, username, password, url ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val database = MemoAmberDatabase.getDatabase(context)
                            val dao = database.vaultItemDao()
                            
                            val newItem = VaultItem(
                                title = title,
                                category = category,
                                content = content,
                                username = username,
                                password = password,
                                url = url,
                                timestamp = System.currentTimeMillis()
                            )
                            
                            dao.insertItem(newItem)
                            loadItems() // 刷新数据
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    showAddDialog = false
                }
            }
        )
    }

    // 编辑对话框
    editingItem?.let { item ->
        EditVaultItemDialog(
            item = item,
            onDismiss = { editingItem = null },
            onSave = { title, category, content, username, password, url ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val database = MemoAmberDatabase.getDatabase(context)
                            val dao = database.vaultItemDao()
                            
                            val updatedItem = VaultItem(
                                id = item.id,
                                title = title,
                                category = item.category,
                                content = content,
                                username = username,
                                password = if (password.isNotBlank()) password else item.password,
                                url = item.url,
                                timestamp = item.timestamp,
                                isEncrypted = item.isEncrypted
                            )
                            
                            dao.updateItem(updatedItem)
                            loadItems() // 刷新数据
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    editingItem = null
                }
            },
            onDelete = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val database = MemoAmberDatabase.getDatabase(context)
                            val dao = database.vaultItemDao()
                            dao.deleteItem(item)
                            loadItems() // 刷新数据
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    editingItem = null
                }
            }
        )
    }

    // 左滑删除确认
    pendingDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("删除项目") },
            text = { Text("确定要删除「${item.title}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            try {
                                val database = MemoAmberDatabase.getDatabase(context)
                                val dao = database.vaultItemDao()
                                dao.deleteItem(item)
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                        loadItems()
                        pendingDelete = null
                        snackbarHostState.showSnackbar("项目已删除")
                    }
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("取消") } }
        )
    }
}

@Composable
fun VaultItemCard(item: VaultItem, onClick: () -> Unit) {
    val categoryIcon = when (item.category) {
        "accounts" -> Icons.Default.Person
        "documents" -> Icons.Default.Description
        "notes" -> Icons.Default.Note
        "cards" -> Icons.Default.CreditCard
        else -> Icons.Default.Lock
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (item.content.isNotBlank()) {
                    Text(
                        text = item.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

// 简化版的添加对话框
@Composable
fun AddVaultItemFullDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("accounts") }
    var content by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Text("添加保险箱项目",
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
                    label = { Text("内容") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
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
                            if (title.isNotBlank()) {
                                onSave(title, category, content, username, password, url)
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}

@Composable
fun EditVaultItemDialog(
    item: VaultItem,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(item.title) }
    var content by remember { mutableStateOf(item.content) }
    var username by remember { mutableStateOf(item.username) }
    var password by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Text("编辑项目",
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
                    label = { Text("内容") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("新密码 (留空不变)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("删除")
                    }
                    
                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("取消")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    onSave(title, item.category, content, username, 
                                        if (password.isNotBlank()) password else item.password, 
                                        item.url)
                                }
                            },
                            enabled = title.isNotBlank()
                        ) {
                            Text("保存")
                        }
                    }
                }
            }
        }
    }

    // 删除确认
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("删除项目") },
            text = { Text("确定要删除「${item.title}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = { onDelete() }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("取消") } }
        )
    }
}
