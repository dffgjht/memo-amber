package com.memoamber.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.memoamber.data.MemoAmberDatabase
import com.memoamber.data.entities.Contact
import com.memoamber.ui.components.SwipeToDeleteContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showEditDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<Contact?>(null) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var pendingDelete by remember { mutableStateOf<Contact?>(null) }
    var showActionMenu by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val contactsFlow = remember { MutableStateFlow<List<Contact>>(emptyList()) }
    val contacts by contactsFlow.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    fun loadContacts() {
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val dao = MemoAmberDatabase.getDatabase(context).contactDao()
                    contactsFlow.value = dao.getAllContactsSync()
                } catch (e: Exception) { e.printStackTrace() }
                finally { isLoading = false }
            }
        }
    }

    LaunchedEffect(Unit) { loadContacts() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("\uD83E\uDD1D \u5173\u7CFB\u4EBA", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editingContact = null; showEditDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(18.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加关系人")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (contacts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(30.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("\uD83E\uDD1D", fontSize = 48.sp)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("还没有关系人", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "记录重要的人，遗言联络更快捷",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(contacts, key = { it.id }) { contact ->
                    SwipeToDeleteContainer(
                        onDelete = { pendingDelete = contact }
                    ) {
                        ContactCard(
                            contact = contact,
                            onClick = { selectedContact = contact; showActionMenu = true },
                            onEdit = { editingContact = contact; showEditDialog = true },
                            onDelete = { pendingDelete = contact }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // 编辑对话框
    if (showEditDialog) {
        ContactEditDialog(
            initial = editingContact,
            onDismiss = { showEditDialog = false },
            onSave = { contact ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val dao = MemoAmberDatabase.getDatabase(context).contactDao()
                            if (contact.id > 0) dao.updateContact(contact) else dao.insertContact(contact)
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                    loadContacts()
                    showEditDialog = false
                    snackbarHostState.showSnackbar(if (contact.id > 0) "关系人已更新" else "关系人已添加")
                }
            }
        )
    }

    // 操作菜单（快速发送/编辑/删除）
    selectedContact?.let { contact ->
        if (showActionMenu) {
            AlertDialog(
                onDismissRequest = { showActionMenu = false; selectedContact = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (contact.avatarPath.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(Uri.parse(contact.avatarPath))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = contact.name,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Text(contact.name)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "关系：${contact.relationship.ifBlank { "未填写" }}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (contact.phone.isNotBlank()) {
                            Text("📱 ${contact.phone}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (contact.email.isNotBlank()) {
                            Text("📧 ${contact.email}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                },
                confirmButton = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (contact.phone.isNotBlank()) {
                            TextButton(onClick = {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}")))
                                showActionMenu = false; selectedContact = null
                            }) { Text("📞 打电话", color = MaterialTheme.colorScheme.primary) }
                            TextButton(onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("smsto:${contact.phone}")
                                    putExtra("sms_body", "")
                                }
                                try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
                                showActionMenu = false; selectedContact = null
                            }) { Text("💬 发短信", color = MaterialTheme.colorScheme.primary) }
                        }
                        if (contact.email.isNotBlank()) {
                            TextButton(onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${contact.email}")
                                    putExtra(Intent.EXTRA_SUBJECT, "")
                                }
                                try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
                                showActionMenu = false; selectedContact = null
                            }) { Text("✉️ 发邮件", color = MaterialTheme.colorScheme.primary) }
                        }
                        TextButton(onClick = {
                            showActionMenu = false; selectedContact = null
                            editingContact = contact; showEditDialog = true
                        }) { Text("✏️ 编辑档案") }
                        TextButton(onClick = {
                            showActionMenu = false; selectedContact = null
                            pendingDelete = contact
                        }) { Text("🗑️ 删除", color = MaterialTheme.colorScheme.error) }
                        TextButton(onClick = { showActionMenu = false; selectedContact = null }) { Text("取消") }
                    }
                }
            )
        }
    }

    // 删除确认
    pendingDelete?.let { contact ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("删除关系人") },
            text = { Text("确定要删除「${contact.name}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            try { MemoAmberDatabase.getDatabase(context).contactDao().deleteContactById(contact.id) }
                            catch (e: Exception) { e.printStackTrace() }
                        }
                        loadContacts()
                        pendingDelete = null
                        snackbarHostState.showSnackbar("关系人已删除")
                    }
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("取消") } }
        )
    }
}

@Composable
fun ContactCard(
    contact: Contact,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像（有图显示图片，无图显示首字母）
            if (contact.avatarPath.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(Uri.parse(contact.avatarPath))
                        .crossfade(true)
                        .build(),
                    contentDescription = contact.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            contactAvatarColor(contact.name),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (contact.relationship.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = contact.relationship,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildString {
                        if (contact.phone.isNotBlank()) append("📱 ${contact.phone}")
                        if (contact.email.isNotBlank()) {
                            if (isNotEmpty()) append("   ")
                            append("✉️ ${contact.email}")
                        }
                        if (isEmpty()) append("未填写联系方式")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
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
    }
}

private fun contactAvatarColor(name: String): Color {
    val colors = listOf(
        Color(0xFFB45309), Color(0xFF6366F1), Color(0xFFEC4899),
        Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFF8B5CF6)
    )
    val index = (name.hashCode() % colors.size + colors.size) % colors.size
    return colors[index]
}

@Composable
fun ContactEditDialog(
    initial: Contact?,
    onDismiss: () -> Unit,
    onSave: (Contact) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var relationship by remember { mutableStateOf(initial?.relationship ?: "") }
    var basicInfo by remember { mutableStateOf(initial?.basicInfo ?: "") }
    var phone by remember { mutableStateOf(initial?.phone ?: "") }
    var email by remember { mutableStateOf(initial?.email ?: "") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    var avatarPath by remember { mutableStateOf(initial?.avatarPath ?: "") }
    val context = LocalContext.current

    // 头像选择器
    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {}
            avatarPath = uri.toString()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Text(
                        if (initial == null) "\uD83E\uDD1D 添加关系人" else "\uD83D\uDCDD 编辑关系人",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    TextButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(
                                    Contact(
                                        id = initial?.id ?: 0,
                                        name = name.trim(),
                                        basicInfo = basicInfo.trim(),
                                        phone = phone.trim(),
                                        email = email.trim(),
                                        relationship = relationship.trim(),
                                        notes = notes.trim(),
                                        avatarPath = avatarPath,
                                        timestamp = initial?.timestamp ?: System.currentTimeMillis()
                                    )
                                )
                            }
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Text(
                            "保存",
                            fontWeight = FontWeight.Bold,
                            color = if (name.isNotBlank()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 头像上传区
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .clickable { avatarPickerLauncher.launch("image/*") }
                        ) {
                            if (avatarPath.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(Uri.parse(avatarPath))
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "头像",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            if (name.isBlank()) MaterialTheme.colorScheme.surfaceVariant
                                            else contactAvatarColor(name),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (name.isBlank()) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        Text(
                                            text = name.take(1).uppercase(),
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                            // 相机角标
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = "更换头像",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                    Text(
                        text = "点击头像从相册选择（留空则显示姓名首字母）",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("姓名 *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = relationship,
                        onValueChange = { relationship = it },
                        label = { Text("双方关系（如：家人/朋友/同事）") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = basicInfo,
                        onValueChange = { basicInfo = it },
                        label = { Text("基本信息（生日、职业等）") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("手机号") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                        ),
                        leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("邮箱") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                        ),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("备注") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }
    }
}
