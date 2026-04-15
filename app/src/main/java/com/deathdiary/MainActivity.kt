package com.deathdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deathdiary.ui.screens.*
import com.deathdiary.data.entities.CommunityPost
import com.deathdiary.data.entities.CommunityComment
import com.deathdiary.data.entities.User
import com.deathdiary.ui.theme.DeathDiaryTheme
import com.deathdiary.security.BiometricAuthManager
import com.deathdiary.security.SecurityManager

class MainActivity : ComponentActivity() {
    private lateinit var securityManager: SecurityManager
    private lateinit var biometricAuthManager: BiometricAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        securityManager = SecurityManager(this)
        biometricAuthManager = BiometricAuthManager(this)

        setContent {
            DeathDiaryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "lock_screen"
                    ) {
                        composable("lock_screen") {
                            LockScreen(
                                onAuthSuccess = {
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

                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onLogout = {
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
}
