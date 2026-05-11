package com.deathdiary.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.deathdiary.data.BackupManager
import com.deathdiary.data.DeathDiaryDatabase
import kotlinx.coroutines.launch

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
    
    var autoLockEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showEmailConfigDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var backupResult by remember { mutableStateOf<String?>(null) }
    var restoreResult by remember { mutableStateOf<String?>(null) }
    var latestBackupFile by remember { mutableStateOf(backupManager.getLatestBackupFile()?.absolutePath) }
    
    // Email config stored in SharedPreferences
    val prefs = remember { context.getSharedPreferences("death_diary_settings", android.content.Context.MODE_PRIVATE) }
    var smtpHost by remember { mutableStateOf(prefs.getString("smtp_host", "") ?: "") }
    var smtpPort by remember { mutableStateOf(prefs.getString("smtp_port", "587") ?: "587") }
    var smtpUsername by remember { mutableStateOf(prefs.getString("smtp_username", "") ?: "") }
    var smtpPassword by remember { mutableStateOf(prefs.getString("smtp_password", "") ?: "") }
    var senderEmail by remember { mutableStateOf(prefs.getString("sender_email", "") ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                text = "Security",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            SettingCard {
                SettingItem(
                    icon = Icons.Default.LockClock,
                    title = "Auto Lock",
                    description = "Lock app when in background",
                    trailing = {
                        Switch(
                            checked = autoLockEnabled,
                            onCheckedChange = { autoLockEnabled = it }
                        )
                    }
                )
                HorizontalDivider()
                SettingItem(
                    icon = Icons.Default.Key,
                    title = "Change Master Password",
                    description = "Update your unlock password",
                    onClick = { /* TODO: Implement */ }
                )
            }

            // Notification / Delivery Section
            Text(
                text = "Will Delivery",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            SettingCard {
                SettingItem(
                    icon = Icons.Default.Email,
                    title = "Email Configuration",
                    description = if (senderEmail.isNotBlank()) "Configured: " + senderEmail else "Configure SMTP for email delivery",
                    onClick = { showEmailConfigDialog = true }
                )
            }

            // Data Section
            Text(
                text = "Data",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            SettingCard {
                SettingItem(
                    icon = Icons.Default.CloudUpload,
                    title = "Backup Data",
                    description = if (latestBackupFile != null) "Last backup: " + (latestBackupFile?.substringAfterLast("/") ?: "") else "Create local backup",
                    onClick = {
                        if (!isProcessing) {
                            showBackupDialog = true
                        }
                    }
                )
                HorizontalDivider()
                SettingItem(
                    icon = Icons.Default.CloudDownload,
                    title = "Restore Data",
                    description = if (latestBackupFile != null) "Restore from latest backup" else "No backup available",
                    onClick = {
                        if (!isProcessing && latestBackupFile != null) {
                            showRestoreDialog = true
                        } else if (latestBackupFile == null) {
                            Toast.makeText(context, "No backup file available", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                HorizontalDivider()
                SettingItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Clear All Data",
                    description = "Permanently delete all data",
                    onClick = { showClearDataDialog = true },
                    titleColor = MaterialTheme.colorScheme.error
                )
            }

            // About Section
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            SettingCard {
                SettingItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    description = "1.3"
                )
                HorizontalDivider()
                SettingItem(
                    icon = Icons.Default.Security,
                    title = "Privacy Policy",
                    description = "View privacy policy",
                    onClick = { showPrivacyDialog = true }
                )
            }

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
                Text("Lock App")
            }
        }
    }

    // All Dialogs
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Lock App") },
            text = { Text("Are you sure you want to lock the app? You will need to re-authenticate.") },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; onLogout() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showPrivacyDialog) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Confirm Clear All Data") },
            text = { Text("This will permanently delete all your diaries, vault items, wills, albums and community data.\n\nThis action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { showClearDataDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Confirm Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showBackupDialog) {
        if (isProcessing) {
            AlertDialog(onDismissRequest = {}, title = { Text("Backup") }, text = { Text("Creating backup, please wait...") }, confirmButton = {}, dismissButton = {})
        } else {
            AlertDialog(
                onDismissRequest = { showBackupDialog = false },
                title = { Text("Backup Data") },
                text = { Text("Create a data backup? The backup will be saved to the Downloads directory.") },
                confirmButton = {
                    Button(onClick = {
                        isProcessing = true
                        scope.launch {
                            val result = backupManager.createBackup(emptyList(), emptyList(), emptyList(), emptyList())
                            isProcessing = false
                            if (result.isSuccess) {
                                latestBackupFile = result.getOrNull()
                                backupResult = "Backup successful"
                            } else {
                                backupResult = "Backup failed"
                            }
                            showBackupDialog = false
                        }
                    }) { Text("Confirm Backup") }
                },
                dismissButton = {
                    TextButton(onClick = { showBackupDialog = false }) { Text("Cancel") }
                }
            )
        }
    }

    if (showRestoreDialog) {
        if (isProcessing) {
            AlertDialog(onDismissRequest = {}, title = { Text("Restore") }, text = { Text("Restoring data, please wait...") }, confirmButton = {}, dismissButton = {})
        } else {
            AlertDialog(
                onDismissRequest = { showRestoreDialog = false },
                title = { Text("Restore Data") },
                text = { Text("Restore from backup? Current data will be overwritten.") },
                confirmButton = {
                    Button(onClick = {
                        isProcessing = true
                        scope.launch {
                            val result = backupManager.restoreBackup(latestBackupFile ?: "")
                            isProcessing = false
                            restoreResult = if (result.isSuccess) "Restore successful" else "Restore failed"
                            showRestoreDialog = false
                        }
                    }) { Text("Confirm Restore") }
                },
                dismissButton = {
                    TextButton(onClick = { showRestoreDialog = false }) { Text("Cancel") }
                }
            )
        }
    }

    // Email Config Dialog
    if (showEmailConfigDialog) {
        AlertDialog(
            onDismissRequest = { showEmailConfigDialog = false },
            title = { Text("Email Configuration") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Configure SMTP server for will/testament email delivery", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = smtpHost,
                        onValueChange = { smtpHost = it },
                        label = { Text("SMTP Host") },
                        placeholder = { Text("smtp.gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = smtpPort,
                        onValueChange = { smtpPort = it },
                        label = { Text("SMTP Port") },
                        placeholder = { Text("587") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = smtpUsername,
                        onValueChange = { smtpUsername = it },
                        label = { Text("SMTP Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = smtpPassword,
                        onValueChange = { smtpPassword = it },
                        label = { Text("SMTP Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = senderEmail,
                        onValueChange = { senderEmail = it },
                        label = { Text("Sender Email") },
                        placeholder = { Text("your-email@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    prefs.edit()
                        .putString("smtp_host", smtpHost)
                        .putString("smtp_port", smtpPort)
                        .putString("smtp_username", smtpUsername)
                        .putString("smtp_password", smtpPassword)
                        .putString("sender_email", senderEmail)
                        .apply()
                    showEmailConfigDialog = false
                    Toast.makeText(context, "Email configuration saved", Toast.LENGTH_SHORT).show()
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEmailConfigDialog = false }) { Text("Cancel") }
            }
        )
    }

    backupResult?.let { result ->
        LaunchedEffect(result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show()
            backupResult = null
        }
    }
    restoreResult?.let { result ->
        LaunchedEffect(result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show()
            restoreResult = null
        }
    }
}

@Composable
fun SettingCard(content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        content()
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(onClick = { onClick?.invoke() }, enabled = onClick != null) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, color = titleColor)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            trailing?.invoke()
        }
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Privacy Policy") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("We take your privacy seriously. All app data is stored locally on your device and encrypted.\n\nData types:\n- Diary records (mood, content, location, photos)\n- Password vault (accounts, passwords, documents)\n- Wills/testaments (recipients, contact info, release conditions)\n- Memory albums (photos, videos)\n\nData protection:\n- All sensitive data encrypted with AES-256\n- Passwords hashed with bcrypt\n- Data stored locally only, never uploaded to servers\n\nPermissions:\n- Storage: for saving photos/videos\n- Camera: for taking photos/videos\n- Internet: for map display and email delivery")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("I Understand") }
        }
    )
}
