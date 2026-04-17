package com.deathdiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.deathdiary.data.entities.Will
import com.deathdiary.utils.WillSender
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WillScreen(onNavigateBack: () -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var emailConfig by remember {
        mutableStateOf<WillSender.EmailConfig?>(null)
    }
    var smsConfig by remember {
        mutableStateOf<WillSender.SmsConfig?>(null)
    }

    val wills = remember {
        mutableStateListOf(
            Will(
                id = 1,
                title = "给家人的信",
                content = "亲爱的家人，如果你们看到这封信...",
                recipientName = "家人",
                recipientContact = "family@example.com",
                contactType = "email",
                releaseCondition = "date",
                releaseDate = System.currentTimeMillis() + 31536000000L,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("遗嘱 / 遗言", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showConfigDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置发送服务")
                    }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "重要提示",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "这些信息将在您指定的时间自动发送给收件人。使用短信/邮箱通知需要提前配置好相关服务。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showConfigDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "发送服务配置",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = buildString {
                                    if (emailConfig != null) append("✓ 邮箱 ")
                                    if (smsConfig != null) append("✓ 短信 ")
                                    if (emailConfig == null && smsConfig == null) append("未配置")
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ChevronRight, null)
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
                }
            } else {
                items(wills) { will ->
                    WillCard(
                        will = will,
                        onSendNow = {
                            scope.launch {
                                val success = when (will.contactType) {
                                    "email" -> WillSender.sendEmail(context, will, emailConfig)
                                    "sms" -> WillSender.sendSms(context, will, smsConfig)
                                    else -> false
                                }
                                if (success) {
                                    val idx = wills.indexOf(will)
                                    if (idx >= 0) {
                                        wills[idx] = will.copy(isReleased = true)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddWillFullDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, content, recipientName, recipientContact, contactType, releaseCondition, releaseDate ->
                wills.add(
                    Will(
                        id = System.currentTimeMillis(),
                        title = title,
                        content = content,
                        recipientName = recipientName,
                        recipientContact = recipientContact,
                        contactType = contactType,
                        releaseCondition = releaseCondition,
                        releaseDate = releaseDate,
                        timestamp = System.currentTimeMillis()
                    )
                )
                showAddDialog = false
            }
        )
    }

    if (showConfigDialog) {
        WillSenderConfigDialog(
            emailConfig = emailConfig,
            smsConfig = smsConfig,
            onDismiss = { showConfigDialog = false },
            onSave = { newEmailConfig, newSmsConfig ->
                emailConfig = newEmailConfig
                smsConfig = newSmsConfig
                showConfigDialog = false
            }
        )
    }
}

@Composable
fun WillCard(will: Will, onSendNow: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (will.isReleased)
                MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
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
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (will.contactType) {
                            "sms" -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    ) {
                        Text(
                            text = if (will.contactType == "sms") "📱短信" else "📧邮箱",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (will.isReleased) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "已发布",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = will.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (will.contactType == "sms") "📱 " else "📧 ",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = will.recipientName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = will.recipientContact,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                if (!will.isReleased) {
                    Button(
                        onClick = onSendNow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("立即发送", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WillSenderConfigDialog(
    emailConfig: WillSender.EmailConfig?,
    smsConfig: WillSender.SmsConfig?,
    onDismiss: () -> Unit,
    onSave: (WillSender.EmailConfig?, WillSender.SmsConfig?) -> Unit
) {
    var smtpHost by remember { mutableStateOf(emailConfig?.smtpHost ?: "smtp.gmail.com") }
    var smtpPort by remember { mutableStateOf(emailConfig?.smtpPort?.toString() ?: "587") }
    var emailUsername by remember { mutableStateOf(emailConfig?.username ?: "") }
    var emailPassword by remember { mutableStateOf(emailConfig?.password ?: "") }
    var senderEmail by remember { mutableStateOf(emailConfig?.senderEmail ?: "") }

    var smsApiUrl by remember { mutableStateOf(smsConfig?.apiUrl ?: "") }
    var smsApiKey by remember { mutableStateOf(smsConfig?.apiKey ?: "") }
    var smsSenderName by remember { mutableStateOf(smsConfig?.senderName ?: "") }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("配置发送服务", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "邮箱配置 (SMTP)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = smtpHost,
                        onValueChange = { smtpHost = it },
                        label = { Text("SMTP 服务器") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = smtpPort,
                            onValueChange = { smtpPort = it },
                            label = { Text("端口") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = senderEmail,
                            onValueChange = { senderEmail = it },
                            label = { Text("发件人邮箱") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = emailUsername,
                        onValueChange = { emailUsername = it },
                        label = { Text("用户名") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = emailPassword,
                        onValueChange = { emailPassword = it },
                        label = { Text("密码/应用专用密码") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Divider()

                    Text(
                        "短信配置",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = smsApiUrl,
                        onValueChange = { smsApiUrl = it },
                        label = { Text("API URL (可选)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = smsApiKey,
                        onValueChange = { smsApiKey = it },
                        label = { Text("API Key (可选)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = smsSenderName,
                        onValueChange = { smsSenderName = it },
                        label = { Text("发送者名称") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(modifier = Modifier.weight(1f), onClick = onDismiss) {
                            Text("取消")
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val newEmailConfig = if (smtpHost.isNotBlank() && emailUsername.isNotBlank()) {
                                    WillSender.EmailConfig(
                                        smtpHost = smtpHost,
                                        smtpPort = smtpPort.toIntOrNull() ?: 587,
                                        username = emailUsername,
                                        password = emailPassword,
                                        senderEmail = senderEmail.ifBlank { emailUsername },
                                        useSsl = true
                                    )
                                } else null

                                val newSmsConfig = if (smsApiUrl.isNotBlank()) {
                                    WillSender.SmsConfig(
                                        apiUrl = smsApiUrl,
                                        apiKey = smsApiKey,
                                        senderName = smsSenderName
                                    )
                                } else null

                                onSave(newEmailConfig, newSmsConfig)
                            }
                        ) {
                            Text("保存配置")
                        }
                    }
                }
            }
        }
    }
}
