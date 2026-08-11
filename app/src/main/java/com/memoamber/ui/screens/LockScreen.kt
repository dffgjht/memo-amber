package com.memoamber.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.memoamber.security.BiometricAuthManager
import com.memoamber.security.SecurityManager
import com.memoamber.ui.components.AmberButton
import com.memoamber.ui.components.AmberGradients
import com.memoamber.ui.components.AmberTextField

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

    // 入场动画
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(
            "memoamber_settings", android.content.Context.MODE_PRIVATE
        )
        val biometricEnabled = prefs.getBoolean("biometric_enabled", true)
        if (!isFirstTime && biometricEnabled && biometricAuthManager.canAuthenticate()) {
            showBiometricPrompt = true
        }
    }

    if (showBiometricPrompt) {
        val activity = context as? FragmentActivity
        if (activity != null) {
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
        } else {
            showBiometricPrompt = false
            biometricError = "生物识别不可用"
        }
    }

    // 全屏渐变背景
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmberGradients.dark),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(800, easing = FastOutSlowInEasing)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🧊",
                        fontSize = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "记忆琥珀",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isFirstTime) "设置主密码来保护您的数据" else "输入密码解锁",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 密码输入区 — 白色半透明卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.12f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 主密码
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = "" },
                            label = { Text("主密码", color = Color.White.copy(alpha = 0.7f)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF59E0B),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFF59E0B),
                                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B)
                                )
                            }
                        )

                        // 确认密码（首次设置时）
                        AnimatedVisibility(visible = isFirstTime) {
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it; errorMessage = "" },
                                label = { Text("确认密码", color = Color.White.copy(alpha = 0.7f)) },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFF59E0B),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color(0xFFF59E0B),
                                    focusedContainerColor = Color.White.copy(alpha = 0.08f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                                    errorBorderColor = Color(0xFFEF4444),
                                    errorCursorColor = Color(0xFFEF4444)
                                ),
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B)
                                    )
                                }
                            )
                        }

                        // 错误信息
                        AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFFCA5A5),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 解锁按钮
                Button(
                    onClick = {
                        if (isFirstTime) {
                            if (password.length >= 6 && password == confirmPassword) {
                                securityManager.setMasterPassword(password)
                                onAuthSuccess()
                            } else if (password.length < 6) {
                                errorMessage = "密码至少需要6个字符"
                            } else {
                                errorMessage = "两次密码不一致"
                            }
                        } else {
                            if (securityManager.verifyMasterPassword(password)) {
                                onAuthSuccess()
                            } else {
                                errorMessage = "密码错误，请重试"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B),
                        contentColor = Color(0xFF78350F),
                        disabledContainerColor = Color.White.copy(alpha = 0.15f),
                        disabledContentColor = Color.White.copy(alpha = 0.4f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 3.dp
                    ),
                    enabled = if (isFirstTime) {
                        password.length >= 6 && password == confirmPassword
                    } else {
                        password.isNotEmpty()
                    }
                ) {
                    Icon(
                        if (isFirstTime) Icons.Default.Check else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFirstTime) "设置密码" else "解锁",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 生物识别按钮
                if (!isFirstTime && biometricAuthManager.canAuthenticate()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showBiometricPrompt = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White.copy(alpha = 0.9f)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                Color.White.copy(alpha = 0.3f)
                            )
                        )
                    ) {
                        Icon(
                            Icons.Default.Fingerprint,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("使用生物识别")
                    }
                }

                // 版本号
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "v1.5.0 · 数据本地加密",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }
        }
    }
}
