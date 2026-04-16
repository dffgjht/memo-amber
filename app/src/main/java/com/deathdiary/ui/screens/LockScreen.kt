package com.deathdiary.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.deathdiary.security.BiometricAuthManager
import com.deathdiary.security.SecurityManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockScreen(
    onAuthSuccess: () -> Unit,
    biometricAuthManager: BiometricAuthManager,
    securityManager: SecurityManager
) {
    var password by remember { mutableStateOf("") }
    var isFirstTime by remember { mutableStateOf(!securityManager.hasMasterPassword()) }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showBiometricPrompt by remember { mutableStateOf(false) }
    var biometricError by remember { mutableStateOf("") }

    // Auto-trigger biometric if available and not first time
    LaunchedEffect(Unit) {
        if (!isFirstTime && biometricAuthManager.canAuthenticate()) {
            showBiometricPrompt = true
        }
    }

    // Real biometric authentication call
    if (showBiometricPrompt) {
        val activity = LocalContext.current as FragmentActivity
        biometricAuthManager.authenticate(
            activity = activity,
            onSuccess = {
                showBiometricPrompt = false
                onAuthSuccess()
            },
            onFailure = { error ->
                showBiometricPrompt = false
                biometricError = error
            }
        )
        showBiometricPrompt = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("回忆录") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isFirstTime) "设置主密码" else "解锁",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isFirstTime) {
                Text(
                    text = "首次使用，请设置一个强密码来保护您的数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("主密码") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("确认密码") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                    modifier = Modifier.fillMaxWidth()
                )

                if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                    Text(
                        text = "密码不匹配",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("输入密码") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = errorMessage.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isFirstTime) {
                        if (password.length >= 6 && password == confirmPassword) {
                            securityManager.setMasterPassword(password)
                            onAuthSuccess()
                        } else if (password.length < 6) {
                            errorMessage = "密码至少需要6个字符"
                        }
                    } else {
                        if (securityManager.verifyMasterPassword(password)) {
                            onAuthSuccess()
                        } else {
                            errorMessage = "密码错误"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = if (isFirstTime) {
                    password.length >= 6 && password == confirmPassword
                } else {
                    password.isNotEmpty()
                }
            ) {
                Text(
                    text = if (isFirstTime) "设置密码" else "解锁",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (!isFirstTime && biometricAuthManager.canAuthenticate()) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showBiometricPrompt = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("使用生物识别")
                }
            }
        }
    }
}
