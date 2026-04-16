package com.deathdiary.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.deathdiary.data.BackupManager
import com.deathdiary.data.DeathDiaryDatabase
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { DeathDiaryDatabase.getDatabase(context) }
    val backupManager = remember { BackupManager(context) }
    
    var biometricEnabled by remember { mutableStateOf(true) }
    var autoLockEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var backupResult by remember { mutableStateOf<String?>(null) }
    var restoreResult by remember { mutableStateOf<String?>(null) }
    var latestBackupFile by remember { mutableStateOf(backupManager.getLatestBackupFile()?.absolutePath) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Security Section
            Text(
                text = "安全",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            SettingCard {
                SettingItem(
                    icon = Icons.Default.Fingerprint,
                    title = "生物识别",
                    description = "使用指纹或面容解锁",
                    trailing = {
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = { biometricEnabled = it }
                        )
                    }
                )
                Divider()
                SettingItem(
                    icon = Icons.Default.LockClock,
                    title = "自动锁定",
                    description = "应用在后台时自动锁定",
                    trailing = {
                        Switch(
                            checked = autoLockEnabled,
                            onCheckedChange = { autoLockEnabled = it }
                        )
                    }
                )
                Divider()
                SettingItem(
                    icon = Icons.Default.Key,
                    title = "修改主密码",
                    description = "更改解锁密码",
                    onClick = { /* TODO: Implement */ }
                )
            }

            // Data Section
            Text(
                text = "数据",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            SettingCard {
                SettingItem(
                    icon = Icons.Default.CloudUpload,
                    title = "备份数据",
                    description = if (latestBackupFile != null) "上次备份: ${latestBackupFile?.substringAfterLast("/")}" else "创建本地备份",
                    onClick = {
                        if (!isProcessing) {
                            showBackupDialog = true
                        }
                    }
                )
                Divider()
                SettingItem(
                    icon = Icons.Default.CloudDownload,
                    title = "恢复数据",
                    description = if (latestBackupFile != null) "从最新备份恢复" else "暂无备份文件",
                    onClick = {
                        if (!isProcessing && latestBackupFile != null) {
                            showRestoreDialog = true
                        } else if (latestBackupFile == null) {
                            Toast.makeText(context, "暂无备份文件", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                Divider()
                SettingItem(
                    icon = Icons.Default.DeleteForever,
                    title = "清除所有数据",
                    description = "永久删除所有数据",
                    onClick = { showClearDataDialog = true },
                    titleColor = MaterialTheme.colorScheme.error
                )
            }

            // About Section
            Text(
                text = "关于",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            SettingCard {
                SettingItem(
                    icon = Icons.Default.Info,
                    title = "版本",
                    description = "1.2.4"
                )
                Divider()
                SettingItem(
                    icon = Icons.Default.Security,
                    title = "隐私政策",
                    description = "查看隐私政策",
                    onClick = { showPrivacyDialog = true }
                )
            }

            // Logout Button
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("锁定应用")
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("锁定应用") },
                    text = { Text("确定要锁定应用吗？您需要重新验证身份才能访问。") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showLogoutDialog = false
                                onLogout()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("确定")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }

            // 隐私政策对话框
            if (showPrivacyDialog) {
                PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
            }

            // 清除数据确认对话框
            if (showClearDataDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDataDialog = false },
                    title = { Text("⚠️ 确认清除所有数据") },
                    text = { Text("此操作将永久删除您的所有日记、保险箱项目、遗嘱、相册和社区数据。\n\n此操作不可撤销，请谨慎操作！") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showClearDataDialog = false
                                // TODO: 实现清除数据功能
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("确认清除")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDataDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }

            // 备份确认对话框
            if (showBackupDialog) {
                if (isProcessing) {
                    AlertDialog(
                        onDismissRequest = { },
                        title = { Text("备份数据") },
                        text = { Text("正在创建备份，请稍候...") },
                        confirmButton = { },
                        dismissButton = { }
                    )
                } else {
                    AlertDialog(
                        onDismissRequest = { showBackupDialog = false },
                        title = { Text("备份数据") },
                        text = { Text("确定要创建数据备份吗？\n\n备份将保存到手机 Downloads 目录。") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    isProcessing = true
                                    scope.launch {
                                        // 收集数据
                                        val diaryEntries = mutableListOf<com.deathdiary.data.entities.DiaryEntry>()
                                        val vaultItems = mutableListOf<com.deathdiary.data.entities.VaultItem>()
                                        val wills = mutableListOf<com.deathdiary.data.entities.Will>()
                                        val mediaItems = mutableListOf<com.deathdiary.data.entities.MediaItem>()
                                        
                                        database.diaryEntryDao().getAllEntries().collect { diaryEntries.addAll(it) }
                                        database.vaultItemDao().getAllItems().collect { vaultItems.addAll(it) }
                                        database.willDao().getAllWills().collect { wills.addAll(it) }
                                        database.mediaItemDao().getAllItems().collect { mediaItems.addAll(it) }
                                        
                                        val result = backupManager.createBackup(
                                            diaryEntries, vaultItems, wills, mediaItems
                                        )
                                        
                                        isProcessing = false
                                        if (result.isSuccess) {
                                            latestBackupFile = result.getOrNull()
                                            backupResult = "备份成功！\n保存位置: Downloads/${result.getOrNull()?.substringAfterLast("/")}"
                                        } else {
                                            backupResult = "备份失败: ${result.exceptionOrNull()?.message}"
                                        }
                                        showBackupDialog = false
                                    }
                                }
                            ) {
                                Text("确定备份")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showBackupDialog = false }
                            ) {
                                Text("取消")
                            }
                        }
                    )
                }
            }

            // 备份结果对话框
            if (backupResult != null) {
                AlertDialog(
                    onDismissRequest = { backupResult = null },
                    title = { Text("备份结果") },
                    text = { Text(backupResult!!) },
                    confirmButton = {
                        Button(onClick = { backupResult = null }) {
                            Text("确定")
                        }
                    }
                )
            }

            // 恢复确认对话框
            if (showRestoreDialog) {
                if (isProcessing) {
                    AlertDialog(
                        onDismissRequest = { },
                        title = { Text("恢复数据") },
                        text = { Text("正在恢复数据，请稍候...\n\n注意：当前数据将被备份覆盖！") },
                        confirmButton = { },
                        dismissButton = { }
                    )
                } else {
                    AlertDialog(
                        onDismissRequest = { showRestoreDialog = false },
                        title = { Text("恢复数据") },
                        text = { Text("确定要从备份恢复数据吗？\n\n注意：当前数据将被覆盖，请确保已有最新备份！\n\n文件名: ${latestBackupFile?.substringAfterLast("/")}") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    isProcessing = true
                                    scope.launch {
                                        latestBackupFile?.let { path ->
                                            val result = backupManager.readBackup(path)
                                            if (result.isSuccess) {
                                                val backupData = result.getOrNull()!!
                                                
                                                // 清除现有数据并恢复
                                                withContext(Dispatchers.IO) {
                                                    database.diaryEntryDao().getAllEntries().collect { entries ->
                                                        entries.forEach { database.diaryEntryDao().deleteEntry(it) }
                                                    }
                                                    database.vaultItemDao().getAllItems().collect { items ->
                                                        items.forEach { database.vaultItemDao().deleteItem(it) }
                                                    }
                                                    database.willDao().getAllWills().collect { wills ->
                                                        wills.forEach { database.willDao().deleteWill(it) }
                                                    }
                                                    database.mediaItemDao().getAllItems().collect { items ->
                                                        items.forEach { database.mediaItemDao().deleteItem(it) }
                                                    }
                                                    
                                                    // 恢复数据
                                                    backupData.diaryEntries.forEach { b ->
                                                        database.diaryEntryDao().insertEntry(
                                                            com.deathdiary.data.entities.DiaryEntry(
                                                                title = b.title, content = b.content,
                                                                mood = b.mood,
                                                                latitude = b.latitude, longitude = b.longitude,
                                                                locationName = b.locationName, timestamp = b.timestamp,
                                                                mediaPaths = b.mediaPaths
                                                            )
                                                        )
                                                    }
                                                    backupData.vaultItems.forEach { b ->
                                                        database.vaultItemDao().insertItem(
                                                            com.deathdiary.data.entities.VaultItem(
                                                                title = b.title, category = b.category,
                                                                content = b.content, username = b.username,
                                                                password = b.password, url = b.url,
                                                                timestamp = b.timestamp
                                                            )
                                                        )
                                                    }
                                                    backupData.wills.forEach { b ->
                                                        database.willDao().insertWill(
                                                            com.deathdiary.data.entities.Will(
                                                                title = b.title, content = b.content,
                                                                recipientName = b.recipientName,
                                                                recipientContact = b.recipientContact,
                                                                releaseCondition = b.releaseCondition,
                                                                isReleased = b.isReleased, releaseDate = b.releaseDate,
                                                                timestamp = b.timestamp
                                                            )
                                                        )
                                                    }
                                                    backupData.mediaItems.forEach { b ->
                                                        database.mediaItemDao().insertItem(
                                                            com.deathdiary.data.entities.MediaItem(
                                                                title = b.title, description = b.description,
                                                                filePath = b.filePath, type = b.type,
                                                                timestamp = b.timestamp, tags = b.tags
                                                            )
                                                        )
                                                    }
                                                }
                                                restoreResult = "恢复成功！\n请返回各模块查看恢复的数据。"
                                            } else {
                                                restoreResult = "恢复失败: ${result.exceptionOrNull()?.message}"
                                            }
                                        }
                                        isProcessing = false
                                        showRestoreDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("确认恢复")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showRestoreDialog = false }
                            ) {
                                Text("取消")
                            }
                        }
                    )
                }
            }

            // 恢复结果对话框
            if (restoreResult != null) {
                AlertDialog(
                    onDismissRequest = { restoreResult = null },
                    title = { Text("恢复结果") },
                    text = { Text(restoreResult!!) },
                    confirmButton = {
                        Button(onClick = { restoreResult = null }) {
                            Text("确定")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "隐私政策",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }
                Divider()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "📅 最后更新：2026年4月16日",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    
                    Text("欢迎使用存证纪！我们深知隐私对您的重要性，并致力于保护您的个人信息。本隐私政策说明了我们如何收集、使用、存储和保护您的数据。", style = MaterialTheme.typography.bodyMedium)
                    
                    Text("📌 信息收集", style = MaterialTheme.typography.titleMedium)
                    Text("存证纪应用会在您主动输入时收集以下信息：\n• 日记内容、心情记录\n• 密码保险箱中的账号密码信息\n• 数字遗嘱内容\n• 相册中的照片和视频\n• 您选择记录的GPS位置信息\n• 社区互动数据（帖子、评论等）", style = MaterialTheme.typography.bodyMedium)
                    
                    Text("📌 数据存储与安全", style = MaterialTheme.typography.titleMedium)
                    Text("• 您的所有数据存储在本地设备上\n• 采用AES-256加密算法保护敏感数据\n• 使用Android Keystore存储加密密钥\n• 支持生物识别（指纹/面容）验证\n• 主密码使用强哈希保护", style = MaterialTheme.typography.bodyMedium)
                    
                    Text("📌 位置信息", style = MaterialTheme.typography.titleMedium)
                    Text("当您在日记中启用位置记录功能时，应用会获取您的GPS坐标。位置信息仅用于标记日记的记录地点，不会用于任何其他目的，也不会上传到任何服务器。", style = MaterialTheme.typography.bodyMedium)
                    
                    Text("📌 数据共享", style = MaterialTheme.typography.titleMedium)
                    Text("存证纪是一款纯本地化应用，我们承诺：\n• 不会将您的任何个人信息上传至服务器\n• 不会与第三方共享您的数据\n• 不会在您不知情的情况下收集任何信息\n• 完全离线运行（社区功能除外，用户自愿使用）", style = MaterialTheme.typography.bodyMedium)
                    
                    Text("📌 数据删除", style = MaterialTheme.typography.titleMedium)
                    Text("您可以随时通过「设置」→「清除所有数据」删除应用中的所有数据。删除后，数据将无法恢复。", style = MaterialTheme.typography.bodyMedium)
                    
                    Text("📌 隐私政策更新", style = MaterialTheme.typography.titleMedium)
                    Text("我们可能会不时更新本隐私政策。更新时会在应用内公告，请定期查看。如有任何问题，请通过GitHub项目页面联系我们。", style = MaterialTheme.typography.bodyMedium)
                    
                    Text("📌 联系我们", style = MaterialTheme.typography.titleMedium)
                    Text("如对本隐私政策有任何疑问，请访问我们的GitHub项目页面或提交Issue。", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun SettingCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = titleColor
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (trailing != null) {
                trailing()
            } else if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
