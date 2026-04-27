package com.memoamber.desktop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.memoamber.desktop.data.DesktopSecurityManager

@Composable
fun LockScreen(
    securityManager: DesktopSecurityManager,
    onAuthSuccess: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val isFirstTime = remember { !securityManager.hasMasterPassword() }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.width(420.dp).padding(24.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
            Column(modifier = Modifier.padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text("记忆琥珀", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                Text("你的回忆，凝固在时间里", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(32.dp))

                if (isFirstTime) {
                    Text("首次使用，请设置一个强密码（至少6位）", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it; errorMessage = "" }, label = { Text("密码") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("确认密码") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), isError = confirmPassword.isNotEmpty() && password != confirmPassword, modifier = Modifier.fillMaxWidth())
                    if (confirmPassword.isNotEmpty() && password != confirmPassword) Text("密码不匹配", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                } else {
                    Text("请输入密码解锁", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it; errorMessage = "" }, label = { Text("密码") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), isError = errorMessage.isNotEmpty(), modifier = Modifier.fillMaxWidth())
                    if (errorMessage.isNotEmpty()) Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = {
                        if (isFirstTime) {
                            if (password.length >= 6 && password == confirmPassword) { securityManager.setMasterPassword(password); onAuthSuccess(password) }
                            else if (password.length < 6) errorMessage = "密码至少需要6个字符"
                        } else {
                            if (securityManager.verifyMasterPassword(password)) onAuthSuccess(password)
                            else errorMessage = "密码错误"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = if (isFirstTime) password.length >= 6 && password == confirmPassword else password.isNotEmpty()
                ) { Text(if (isFirstTime) "创建密码" else "解锁", style = MaterialTheme.typography.titleMedium) }
            }
        }
    }
}
