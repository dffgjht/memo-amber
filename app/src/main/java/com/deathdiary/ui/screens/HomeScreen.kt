package com.deathdiary.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.deathdiary.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDiary: () -> Unit,
    onNavigateToVault: () -> Unit,
    onNavigateToWill: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("death-diary") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_home_cover),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Welcome Back",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Record memories, secure your legacy",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Features",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                FeatureCardWithIcon(
                    iconRes = R.drawable.ic_diary,
                    title = "Diary",
                    description = "Record your daily thoughts and memories",
                    onClick = onNavigateToDiary,
                    iconTint = Color(0xFFFFD700)
                )
            }

            item {
                FeatureCardWithIcon(
                    iconRes = R.drawable.ic_vault,
                    title = "Password Vault",
                    description = "Securely store accounts and important info",
                    onClick = onNavigateToVault,
                    iconTint = Color(0xFF4CAF50)
                )
            }

            item {
                FeatureCardWithIcon(
                    iconRes = R.drawable.ic_will,
                    title = "Will / Testament",
                    description = "Leave your final words",
                    onClick = onNavigateToWill,
                    iconTint = Color(0xFF9C27B0)
                )
            }

            item {
                FeatureCardWithIcon(
                    iconRes = R.drawable.ic_gallery,
                    title = "Memory Album",
                    description = "Cherished photos and videos",
                    onClick = onNavigateToGallery,
                    iconTint = Color(0xFF2196F3)
                )
            }

            item {
                FeatureCardWithIcon(
                    iconRes = R.drawable.ic_community,
                    title = "Community",
                    description = "Share experiences, get support",
                    onClick = onNavigateToCommunity,
                    iconTint = Color(0xFFFF9800)
                )
            }

            item {
                FeatureCardWithIcon(
                    iconRes = R.drawable.ic_settings,
                    title = "Settings",
                    description = "App settings and security options",
                    onClick = onNavigateToSettings,
                    iconTint = Color(0xFF607D8B)
                )
            }
        }
    }
}

@Composable
fun FeatureCardWithIcon(
    iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit,
    iconTint: Color? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = iconTint?.let { ColorFilter.tint(it) }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
