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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.deathdiary.data.entities.VaultItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(onNavigateBack: () -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var editingItemId by remember { mutableStateOf<Long?>(null) }

    val items = remember {
        mutableStateListOf(
            VaultItem(id = 1, title = "Bank Account", category = "accounts", content = "Main bank account info",
                username = "user123", url = "https://bank.example.com", timestamp = System.currentTimeMillis()),
            VaultItem(id = 2, title = "Insurance Policy", category = "documents", content = "Life insurance info",
                timestamp = System.currentTimeMillis())
        )
    }

    val editingItem = editingItemId?.let { id ->
        try {
            items.find { it.id == id }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val filteredItems = remember(searchQuery, items) {
        try {
            if (searchQuery.isBlank()) items else items.filter { it.title.contains(searchQuery, ignoreCase = true) }
        } catch (e: Exception) {
            e.printStackTrace()
            items
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Password Vault", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = { IconButton(onClick = { editingItemId = null; showAddDialog = true }) { Icon(Icons.Default.Add, "Add") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editingItemId = null; showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    ) { pv ->
        Column(modifier = Modifier.fillMaxSize().padding(pv)) {
            OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it },
                placeholder = { Text("Search vault items...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                modifier = Modifier.fillMaxWidth().padding(16.dp), singleLine = true, shape = RoundedCornerShape(12.dp))

            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Lock, null, Modifier.size(80.dp), tint = MaterialTheme.colorScheme.outline.copy(0.5f))
                        Spacer(Modifier.height(16.dp))
                        Text("Vault is empty", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
                        Text("Tap + to add your first secret", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline.copy(0.7f))
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filteredItems) { item ->
                        VaultItemCard(item = item, onClick = {
                            try {
                                editingItemId = item.id
                                showAddDialog = true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddVaultItemFullDialog(existingItem = editingItem, onDismiss = {
            showAddDialog = false
            editingItemId = null
        },
            onSave = { savedItem ->
                try {
                    val idx = items.indexOfFirst { it.id == savedItem.id }
                    if (idx >= 0) {
                        items[idx] = savedItem
                    } else {
                        items.add(savedItem)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                showAddDialog = false
                editingItemId = null
            })
    }
}

@Composable
fun VaultItemCard(item: VaultItem, onClick: () -> Unit) {
    val categoryIcon = when (item.category) {
        "accounts" -> Icons.Default.Person
        "documents" -> Icons.Default.Description
        "notes" -> Icons.Default.Note
        "cards" -> Icons.Default.CreditCard
        else -> Icons.Default.Lock
    }
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp), onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.Top) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(categoryIcon, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                if (item.content.isNotBlank()) {
                    Text(item.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    Spacer(Modifier.height(4.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (item.username.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(Modifier.width(3.dp))
                            Text(item.username, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                    if (item.url.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(3.dp))
                            Text(item.url.take(30), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddVaultItemFullDialog(existingItem: VaultItem?, onDismiss: () -> Unit, onSave: (VaultItem) -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("accounts") }
    var content by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    // Initialize with existing data when editing
    LaunchedEffect(existingItem) {
        try {
            existingItem?.let {
                title = it.title
                category = it.category
                content = it.content
                username = it.username
                password = it.password
                url = it.url
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val categoryOptions = listOf(Pair("accounts", "\uD83D\uDCB3 Accounts"), Pair("documents", "\uD83D\uDCC4 Documents"),
        Pair("notes", "\uD83D\uDCDD Notes"), Pair("cards", "\uD83D\uDCB3 Cards"))

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MaterialTheme.colorScheme.onPrimaryContainer) }
                    Text(if (existingItem != null) "Edit Item" else "Add Vault Item",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    TextButton(onClick = {
                        try {
                            if (title.isNotBlank()) {
                                val savedItem = VaultItem(
                                    id = if (existingItem != null && existingItem.id > 0) existingItem.id else System.currentTimeMillis(),
                                    title = title, category = category, content = content,
                                    username = username, password = password, url = url,
                                    timestamp = existingItem?.timestamp ?: System.currentTimeMillis()
                                )
                                onSave(savedItem)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, enabled = title.isNotBlank()) {
                        Text("Save", fontWeight = FontWeight.Bold,
                            color = if (title.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                    }
                }
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                    Column {
                        Text("Category", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categoryOptions) { (value, label) ->
                                FilterChip(
                                    selected = category == value,
                                    onClick = { category = value },
                                    label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                                    modifier = Modifier.height(36.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(value = content, onValueChange = { content = it },
                        label = { Text("Content/Notes") }, modifier = Modifier.fillMaxWidth(),
                        minLines = 3, maxLines = 5, shape = RoundedCornerShape(12.dp))

                    OutlinedTextField(value = username, onValueChange = { username = it },
                        label = { Text("Username/Email") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Person, null) })

                    OutlinedTextField(value = password, onValueChange = { password = it },
                        label = { Text("Password") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = if (password.isNotEmpty()) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None)

                    OutlinedTextField(value = url, onValueChange = { url = it },
                        label = { Text("Website URL") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Link, null) })
                }
            }
        }
    }
}
