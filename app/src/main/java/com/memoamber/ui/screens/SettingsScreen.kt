package com.memoamber.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.memoamber.utils.BackupManager
import com.memoamber.data.MemoAmberDatabase
import com.memoamber.security.SecurityManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { MemoAmberDatabase.getDatabase(context) }
    val backupManager = remember { BackupManager(context) }
    val securityManager = remember { SecurityManager(context) }
    val prefs = context.getSharedPreferences("memoamber_settings", android.content.Context.MODE_PRIVATE)

    var biometricEnabled by remember { mutableStateOf(prefs.getBoolean("biometric_enabled", true)) }
    var autoLockEnabled by remember { mutableStateOf(prefs.getBoolean("auto_lock", true)) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var backupResult by remember { mutableStateOf<String?>(null) }
    var restoreResult by remember { mutableStateOf<String?>(null) }
    var latestBackupFile by remember { mutableStateOf<String?>(backupManager.getLocalBackups().firstOrNull()?.absolutePath) }
    var biometricStatus by remember { mutableStateOf("Detecting...") }
    var showBiometricInfo by remember { mutableStateOf(false) }
    var appVersion by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val biometricManager = androidx.biometric.BiometricManager.from(context)
        biometricStatus = when (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> "Fingerprint available"
            else -> when (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> "Face recognition available"
                else -> "Biometric not supported"
            }
        }
        appVersion = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.5.0"
        } catch (e: Exception) { "1.5.0" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("\u8BBE\u7F6E", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Security Section
            SettingsSection(title = "\uD83D\uDD10 \u5B89\u5168") {
                SettingSwitchItem(
                    icon = Icons.Default.Fingerprint,
                    title = "\u751F\u7269\u8BC6\u522B",
                    description = biometricStatus,
                    checked = biometricEnabled,
                    onCheckedChange = {
                        biometricEnabled = it
                        prefs.edit().putBoolean("biometric_enabled", it).apply()
                    }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingSwitchItem(
                    icon = Icons.Default.LockClock,
                    title = "\u81EA\u52A8\u9501\u5B9A",
                    description = "\u79BB\u5F005\u5206\u949F\u540E\u81EA\u52A8\u9501\u5B9A",
                    checked = autoLockEnabled,
                    onCheckedChange = {
                        autoLockEnabled = it
                        prefs.edit().putBoolean("auto_lock", it).apply()
                    }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingClickItem(
                    icon = Icons.Default.Key,
                    title = "\u4FEE\u6539\u4E3B\u5BC6\u7801",
                    description = "\u66F4\u6539\u89E3\u9501\u5BC6\u7801",
                    onClick = { showChangePasswordDialog = true }
                )
            }

            // Data Section
            SettingsSection(title = "\uD83D\uDCBE \u6570\u636E") {
                SettingClickItem(
                    icon = Icons.Default.CloudUpload,
                    title = "\u5907\u4EFD\u6570\u636E",
                    description = if (latestBackupFile != null) "\u4E0A\u6B21: ${latestBackupFile?.substringAfterLast("/")}" else "\u521B\u5EFA\u672C\u5730\u5907\u4EFD",
                    onClick = { if (!isProcessing) showBackupDialog = true }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingClickItem(
                    icon = Icons.Default.CloudDownload,
                    title = "\u6062\u590D\u6570\u636E",
                    description = if (latestBackupFile != null) "\u4ECE\u6700\u65B0\u5907\u4EFD\u6062\u590D" else "\u6682\u65E0\u5907\u4EFD",
                    onClick = {
                        if (!isProcessing && latestBackupFile != null) showRestoreDialog = true
                        else if (latestBackupFile == null) Toast.makeText(context, "\u6682\u65E0\u5907\u4EFD\u6587\u4EF6", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingClickItem(
                    icon = Icons.Default.DeleteForever,
                    title = "\u6E05\u9664\u6240\u6709\u6570\u636E",
                    description = "\u6C38\u4E45\u5220\u9664\u6240\u6709\u6570\u636E",
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = { showClearDataDialog = true }
                )
            }

            // About Section
            SettingsSection(title = "\u2139\uFE0F \u5173\u4E8E") {
                SettingClickItem(
                    icon = Icons.Default.Info,
                    title = "\u7248\u672C",
                    description = "v${appVersion.ifBlank { "1.5.0" }}"
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingClickItem(
                    icon = Icons.Default.Security,
                    title = "\u9690\u79C1\u653F\u7B56",
                    description = "\u67E5\u770B\u9690\u79C1\u653F\u7B56",
                    onClick = { showPrivacyDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("\u9501\u5B9A\u5E94\u7528", fontWeight = FontWeight.SemiBold)
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("\u9501\u5B9A\u5E94\u7528") },
                    text = { Text("\u786E\u5B9A\u8981\u9501\u5B9A\u5E94\u7528\u5417\uFF1F\u60A8\u9700\u8981\u91CD\u65B0\u9A8C\u8BC1\u8EAB\u4EFD\u624D\u80FD\u8BBF\u95EE\u3002") },
                    confirmButton = {
                        Button(onClick = { showLogoutDialog = false; onLogout() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("\u786E\u5B9A") }
                    },
                    dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("\u53D6\u6D88") } }
                )
            }

            if (showPrivacyDialog) PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })

            if (showBiometricInfo) {
                AlertDialog(
                    onDismissRequest = { showBiometricInfo = false },
                    title = { Text("\uD83D\uDD10 \u751F\u7269\u8BC6\u522B\u8BBE\u7F6E") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("\u5F53\u524D\u8BBE\u7F6E\u4F18\u5148\u7EA7\uFF1A", fontWeight = FontWeight.Bold)
                            Text("1. \u6307\u7EB9\u8BC6\u522B\uFF08\u4F18\u5148\uFF09")
                            Text("2. \u4EBA\u8138\u8BC6\u522B\uFF08\u5907\u9009\uFF09")
                        }
                    },
                    confirmButton = { Button(onClick = { showBiometricInfo = false }) { Text("\u77E5\u9053\u4E86") } }
                )
            }

            if (showChangePasswordDialog) {
                ChangePasswordDialog(
                    securityManager = securityManager,
                    onDismiss = { showChangePasswordDialog = false },
                    onSuccess = {
                        showChangePasswordDialog = false
                        Toast.makeText(context, "\u4E3B\u5BC6\u7801\u5DF2\u66F4\u65B0", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (showClearDataDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDataDialog = false },
                    title = { Text("\u26A0\uFE0F \u786E\u8BA4\u6E05\u9664\u6240\u6709\u6570\u636E") },
                    text = { Text("\u6B64\u64CD\u4F5C\u5C06\u6C38\u4E45\u5220\u9664\u6240\u6709\u6570\u636E\uFF0C\u4E0D\u53EF\u64A4\u9500\uFF01") },
                    confirmButton = {
                        Button(onClick = {
                            showClearDataDialog = false; isProcessing = true
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    try { database.clearAllTables(); backupManager.getLocalBackups().forEach { it.delete() } }
                                    catch (e: Exception) { e.printStackTrace() }
                                }
                                isProcessing = false
                                Toast.makeText(context, "\u6240\u6709\u6570\u636E\u5DF2\u6E05\u9664", Toast.LENGTH_SHORT).show()
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("\u786E\u8BA4\u6E05\u9664") }
                    },
                    dismissButton = { TextButton(onClick = { showClearDataDialog = false }) { Text("\u53D6\u6D88") } }
                )
            }

            if (showBackupDialog) {
                if (isProcessing) {
                    AlertDialog(onDismissRequest = {}, title = { Text("\u5907\u4EFD\u6570\u636E") }, text = { Text("\u6B63\u5728\u521B\u5EFA\u5907\u4EFD...") }, confirmButton = {}, dismissButton = {})
                } else {
                    AlertDialog(
                        onDismissRequest = { showBackupDialog = false },
                        title = { Text("\u5907\u4EFD\u6570\u636E") },
                        text = { Text("\u786E\u5B9A\u8981\u521B\u5EFA\u6570\u636E\u5907\u4EFD\u5417\uFF1F") },
                        confirmButton = {
                            Button(onClick = {
                                isProcessing = true
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        val backupPath = backupManager.exportData()
                                        isProcessing = false
                                        latestBackupFile = backupPath
                                        backupResult = if (backupPath != null) "\u5907\u4EFD\u6210\u529F\uFF01\n$backupPath" else "\u5907\u4EFD\u5931\u8D25"
                                    }
                                    showBackupDialog = false
                                }
                            }) { Text("\u786E\u5B9A\u5907\u4EFD") }
                        },
                        dismissButton = { TextButton(onClick = { showBackupDialog = false }) { Text("\u53D6\u6D88") } }
                    )
                }
            }

            if (backupResult != null) {
                AlertDialog(onDismissRequest = { backupResult = null }, title = { Text("\u5907\u4EFD\u7ED3\u679C") }, text = { Text(backupResult!!) }, confirmButton = { Button(onClick = { backupResult = null }) { Text("\u786E\u5B9A") } })
            }

            if (showRestoreDialog) {
                if (isProcessing) {
                    AlertDialog(onDismissRequest = {}, title = { Text("\u6062\u590D\u6570\u636E") }, text = { Text("\u6B63\u5728\u6062\u590D\u6570\u636E...") }, confirmButton = {}, dismissButton = {})
                } else {
                    val latestBackup = backupManager.getLocalBackups().firstOrNull()
                    AlertDialog(
                        onDismissRequest = { showRestoreDialog = false },
                        title = { Text("\u6062\u590D\u6570\u636E") },
                        text = { Text("\u786E\u5B9A\u8981\u4ECE\u5907\u4EFD\u6062\u590D\u6570\u636E\u5417\uFF1F\u5F53\u524D\u6570\u636E\u5C06\u88AB\u8986\u76D6\uFF01") },
                        confirmButton = {
                            Button(onClick = {
                                latestBackup?.let { backupFile ->
                                    isProcessing = true
                                    scope.launch {
                                        withContext(Dispatchers.IO) {
                                            val success = backupManager.importData(backupFile.absolutePath, merge = false)
                                            isProcessing = false
                                            restoreResult = if (success) "\u6062\u590D\u6210\u529F\uFF01" else "\u6062\u590D\u5931\u8D25"
                                        }
                                        showRestoreDialog = false
                                    }
                                }
                            }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) { Text("\u786E\u8BA4\u6062\u590D") }
                        },
                        dismissButton = { TextButton(onClick = { showRestoreDialog = false }) { Text("\u53D6\u6D88") } }
                    )
                }
            }

            if (restoreResult != null) {
                AlertDialog(onDismissRequest = { restoreResult = null }, title = { Text("\u6062\u590D\u7ED3\u679C") }, text = { Text(restoreResult!!) }, confirmButton = { Button(onClick = { restoreResult = null }) { Text("\u786E\u5B9A") } })
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) { Column(content = content) }
    }
}

@Composable
fun SettingSwitchItem(icon: ImageVector, title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
        ))
    }
}

@Composable
fun SettingClickItem(icon: ImageVector, title: String, description: String, onClick: (() -> Unit)? = null, titleColor: Color = MaterialTheme.colorScheme.onSurface) {
    Surface(onClick = { onClick?.invoke() }, enabled = onClick != null) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = titleColor)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (onClick != null) Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ChangePasswordDialog(securityManager: SecurityManager, onDismiss: () -> Unit, onSuccess: () -> Unit) {
    val scope = rememberCoroutineScope()
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var changing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!changing) onDismiss() },
        title = { Text("\u4FEE\u6539\u4E3B\u5BC6\u7801") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = oldPassword, onValueChange = { oldPassword = it; passwordError = null }, label = { Text("\u5F53\u524D\u5BC6\u7801") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = newPassword, onValueChange = { newPassword = it; passwordError = null }, label = { Text("\u65B0\u5BC6\u7801\uFF08\u81F3\u5C116\u4F4D\uFF09") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = confirmNewPassword, onValueChange = { confirmNewPassword = it; passwordError = null }, label = { Text("\u786E\u8BA4\u65B0\u5BC6\u7801") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), isError = confirmNewPassword.isNotEmpty() && confirmNewPassword != newPassword, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                if (passwordError != null) Text(passwordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(enabled = !changing && oldPassword.isNotBlank() && newPassword.length >= 6 && newPassword == confirmNewPassword, onClick = {
                changing = true
                scope.launch(Dispatchers.IO) {
                    val verified = securityManager.verifyMasterPassword(oldPassword)
                    if (verified) { securityManager.setMasterPassword(newPassword); withContext(Dispatchers.Main) { onSuccess() } }
                    else withContext(Dispatchers.Main) { changing = false; passwordError = "\u5F53\u524D\u5BC6\u7801\u9519\u8BEF" }
                }
            }) { Text(if (changing) "\u4FEE\u6539\u4E2D..." else "\u786E\u8BA4\u4FEE\u6539") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("\u53D6\u6D88") } }
    )
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("\u9690\u79C1\u653F\u7B56", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
                Divider()
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("\u6B22\u8FCE\u4F7F\u7528\u8BB0\u5FC6\u7425\u73C0\uFF08MemoAmber\uFF09\uFF01\u6211\u4EEC\u6DF1\u77E5\u9690\u79C1\u5BF9\u60A8\u7684\u91CD\u8981\u6027\u3002", style = MaterialTheme.typography.bodyMedium)
                    Text("\uD83D\uDCCC \u4FE1\u606F\u6536\u96C6", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("\u2022 \u65E5\u8BB0\u5185\u5BB9\u3001\u5FC3\u60C5\u8BB0\u5F55\n\u2022 \u5BC6\u7801\u4FDD\u9669\u7BB1\u4E2D\u7684\u8D26\u53F7\u5BC6\u7801\n\u2022 \u6570\u5B57\u9057\u5631\u5185\u5BB9\n\u2022 \u76F8\u518C\u4E2D\u7684\u7167\u7247\u548C\u89C6\u9891", style = MaterialTheme.typography.bodyMedium)
                    Text("\uD83D\uDCCC \u6570\u636E\u5B58\u50A8\u4E0E\u5B89\u5168", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("\u2022 \u6240\u6709\u6570\u636E\u5B58\u50A8\u5728\u672C\u5730\u8BBE\u5907\u4E0A\n\u2022 AES-256\u52A0\u5BC6\u7B97\u6CD5\u4FDD\u62A4\u654F\u611F\u6570\u636E\n\u2022 Android Keystore\u5B58\u50A8\u52A0\u5BC6\u5BC6\u94A5\n\u2022 \u652F\u6301\u751F\u7269\u8BC6\u522B\u9A8C\u8BC1", style = MaterialTheme.typography.bodyMedium)
                    Text("\uD83D\uDCCC \u6570\u636E\u5171\u4EAB", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("\u7EAF\u672C\u5730\u5316\u5E94\u7528\uFF1A\u4E0D\u4E0A\u4F20\u3001\u4E0D\u5171\u4EAB\u3001\u4E0D\u6536\u96C6\u3002", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
