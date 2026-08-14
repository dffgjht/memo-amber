package com.memoamber

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.memoamber.ui.screens.*
import com.memoamber.ui.theme.MemoAmberTheme
import com.memoamber.security.BiometricAuthManager
import com.memoamber.security.SecurityManager

class MainActivity : FragmentActivity() {

    private lateinit var securityManager: SecurityManager
    private lateinit var biometricAuthManager: BiometricAuthManager

    // 自动锁定：离开应用超过 AUTO_LOCK_TIMEOUT_MS 后回到前台需要重新解锁
    private val lockedState = mutableStateOf(false)
    private var backgroundSince = 0L

    companion object {
        private const val AUTO_LOCK_TIMEOUT_MS = 5 * 60 * 1000L // 5 分钟
        private const val PREFS_NAME = "memoamber_settings"
        private const val KEY_AUTO_LOCK = "auto_lock"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        securityManager = SecurityManager(this)
        biometricAuthManager = BiometricAuthManager(this)

        setContent {
            MemoAmberTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val locked by lockedState

                    // 自动锁定触发：回到 lock_screen 并清空导航栈
                    LaunchedEffect(locked) {
                        if (locked) {
                            navController.navigate("lock_screen") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "lock_screen"
                    ) {
                        composable("lock_screen") {
                            LockScreen(
                                onAuthSuccess = {
                                    lockedState.value = false
                                    navController.navigate("home") {
                                        popUpTo("lock_screen") { inclusive = true }
                                    }
                                },
                                biometricAuthManager = biometricAuthManager,
                                securityManager = securityManager
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onNavigateToDiary = { navController.navigate("diary") },
                                onNavigateToVault = { navController.navigate("vault") },
                                onNavigateToWill = { navController.navigate("will") },
                                onNavigateToGallery = { navController.navigate("gallery") },
                                onNavigateToCommunity = { navController.navigate("community") },
                                onNavigateToContacts = { navController.navigate("contacts") },
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }

                        composable("diary") {
                            DiaryScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("vault") {
                            VaultScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("will") {
                            WillScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("gallery") {
                            GalleryScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("community") {
                            CommunityScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("contacts") {
                            ContactsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onLogout = {
                                    lockedState.value = true
                                    navController.navigate("lock_screen") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // 记录离开后台的时间（仅当已解锁时）
        if (!lockedState.value && autoLockEnabled()) {
            backgroundSince = System.currentTimeMillis()
        }
    }

    override fun onStart() {
        super.onStart()
        // 回到前台：若超过 5 分钟则锁定
        if (autoLockEnabled() && backgroundSince > 0) {
            val elapsed = System.currentTimeMillis() - backgroundSince
            if (elapsed >= AUTO_LOCK_TIMEOUT_MS) {
                lockedState.value = true
            }
        }
        backgroundSince = 0L
    }

    private fun autoLockEnabled(): Boolean {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_AUTO_LOCK, true)
    }
}
