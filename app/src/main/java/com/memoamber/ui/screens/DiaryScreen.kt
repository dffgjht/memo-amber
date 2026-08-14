package com.memoamber.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.memoamber.data.MemoAmberDatabase
import com.memoamber.data.entities.DiaryEntry
import com.memoamber.ui.components.SwipeToDeleteContainer
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showEditDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var pendingDelete by remember { mutableStateOf<DiaryEntry?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val entriesFlow = remember { MutableStateFlow<List<DiaryEntry>>(emptyList()) }
    val entries by entriesFlow.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    fun loadEntries() {
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val dao = MemoAmberDatabase.getDatabase(context).diaryEntryDao()
                    entriesFlow.value = dao.getAllEntriesSync()
                } catch (e: Exception) { e.printStackTrace() }
                finally { isLoading = false }
            }
        }
    }

    LaunchedEffect(Unit) { loadEntries() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("\uD83D\uDCD6 \u65E5\u8BB0", fontWeight = FontWeight.Bold)
                },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editingEntry = null; showEditDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(18.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Write diary")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (entries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(30.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("\uD83D\uDCDD", fontSize = 48.sp)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("\u8FD8\u6CA1\u6709\u65E5\u8BB0", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "\u70B9\u51FB\u53F3\u4E0B\u89D2\u6309\u94AE\u8BB0\u5F55\u4ECA\u5929\u7684\u5FC3\u60C5",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    SwipeToDeleteContainer(
                        onDelete = { pendingDelete = entry }
                    ) {
                        DiaryEntryCard(
                            entry = entry,
                            onClick = { selectedEntry = entry },
                            onEdit = { editingEntry = entry; showEditDialog = true },
                            onDelete = { pendingDelete = entry }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showEditDialog) {
        DiaryEditDialog(
            initial = editingEntry,
            onDismiss = { showEditDialog = false },
            onSave = { entry ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val dao = MemoAmberDatabase.getDatabase(context).diaryEntryDao()
                            if (entry.id > 0) dao.updateEntry(entry) else dao.insertEntry(entry)
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                    loadEntries()
                    showEditDialog = false
                    snackbarHostState.showSnackbar(if (entry.id > 0) "\u65E5\u8BB0\u5DF2\u4FDD\u5B58" else "\u65B0\u65E5\u8BB0\u5DF2\u521B\u5EFA")
                }
            }
        )
    }

    selectedEntry?.let { entry ->
        DiaryDetailDialog(
            entry = entry,
            onDismiss = { selectedEntry = null },
            onEdit = { selectedEntry = null; editingEntry = entry; showEditDialog = true },
            onDelete = { selectedEntry = null; pendingDelete = entry }
        )
    }

    pendingDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("\u5220\u9664\u65E5\u8BB0") },
            text = { Text("\u786E\u5B9A\u8981\u5220\u9664\u300C${entry.title}\u300D\u5417\uFF1F\u6B64\u64CD\u4F5C\u4E0D\u53EF\u64A4\u9500\u3002") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            try { MemoAmberDatabase.getDatabase(context).diaryEntryDao().deleteEntryById(entry.id) }
                            catch (e: Exception) { e.printStackTrace() }
                        }
                        loadEntries()
                        pendingDelete = null
                        snackbarHostState.showSnackbar("\u65E5\u8BB0\u5DF2\u5220\u9664")
                    }
                }) { Text("\u5220\u9664", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("\u53D6\u6D88") } }
        )
    }
}

@Composable
fun DiaryEntryCard(
    entry: DiaryEntry,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                MoodBadge(mood = entry.mood)
                Spacer(modifier = Modifier.width(4.dp))
                // 卡片快捷操作菜单：编辑 / 删除
                Box {
                    IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "更多操作",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("\u270F\uFE0F \u7F16\u8F91") },
                            onClick = { menuExpanded = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text("\uD83D\uDDD1\uFE0F \u5220\u9664", color = MaterialTheme.colorScheme.error) },
                            onClick = { menuExpanded = false; onDelete() }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null,
                        modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatTimestampFull(entry.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                if (entry.weather.isNotBlank()) {
                    Text(text = weatherEmoji(entry.weather), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun MoodBadge(mood: String) {
    val (emoji, label) = moodLabel(mood)
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = "$emoji $label",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

fun moodLabel(mood: String): Pair<String, String> = when (mood) {
    "happy" -> "\uD83D\uDE0A" to "\u5F00\u5FC3"
    "sad" -> "\uD83D\uDE22" to "\u96BE\u8FC7"
    "neutral" -> "\uD83D\uDE10" to "\u5E73\u9759"
    "excited" -> "\uD83E\uDD29" to "\u5174\u594B"
    "anxious" -> "\uD83D\uDE30" to "\u7126\u8651"
    "angry" -> "\uD83D\uDE20" to "\u751F\u6C14"
    else -> "\uD83D\uDE10" to "\u5E73\u9759"
}

fun weatherEmoji(weather: String): String = when (weather) {
    "sunny" -> "\u2600\uFE0F"; "cloudy" -> "\u26C5"; "rainy" -> "\uD83C\uDF27\uFE0F"
    "snowy" -> "\u2744\uFE0F"; "stormy" -> "\u26C8\uFE0F"; "night" -> "\uD83C\uDF19"
    else -> ""
}

@Composable
fun DiaryEditDialog(
    initial: DiaryEntry?,
    onDismiss: () -> Unit,
    onSave: (DiaryEntry) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var content by remember { mutableStateOf(initial?.content ?: "") }
    var mood by remember { mutableStateOf(initial?.mood ?: "neutral") }
    var weather by remember { mutableStateOf(initial?.weather ?: "sunny") }
    var tags by remember { mutableStateOf(initial?.tags ?: "") }
    var selectedImageUris by remember {
        mutableStateOf(
            runCatching {
                val paths = initial?.mediaPaths
                if (paths.isNullOrBlank()) emptyList()
                else Gson().fromJson(paths, Array<String>::class.java).map { Uri.parse(it) }
            }.getOrDefault(emptyList())
        )
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> selectedImageUris = uris }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("\u53D6\u6D88", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Text(
                        if (initial == null) "\u270F\uFE0F \u5199\u65E5\u8BB0" else "\uD83D\uDCDD \u7F16\u8F91\u65E5\u8BB0",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    TextButton(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onSave(
                                    DiaryEntry(
                                        id = initial?.id ?: 0,
                                        title = title.trim(),
                                        content = content.trim(),
                                        mood = mood,
                                        timestamp = initial?.timestamp ?: System.currentTimeMillis(),
                                        weather = weather,
                                        tags = tags.trim(),
                                        isEncrypted = initial?.isEncrypted ?: true,
                                        mediaPaths = if (selectedImageUris.isEmpty()) initial?.mediaPaths ?: ""
                                        else Gson().toJson(selectedImageUris.map { it.toString() })
                                    )
                                )
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank()
                    ) {
                        Text("\u4FDD\u5B58", fontWeight = FontWeight.Bold,
                            color = if (title.isNotBlank() && content.isNotBlank()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = title, onValueChange = { title = it },
                        label = { Text("\u6807\u9898") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Column {
                        Text("\u4ECA\u5929\u5FC3\u60C5", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf("happy" to "\uD83D\uDE0A\u5F00\u5FC3", "sad" to "\uD83D\uDE22\u96BE\u8FC7",
                                "neutral" to "\uD83D\uDE10\u5E73\u9759", "excited" to "\uD83E\uDD29\u5174\u594B",
                                "anxious" to "\uD83D\uDE30\u7126\u8651", "angry" to "\uD83D\uDE20\u751F\u6C14")) { (value, label) ->
                                FilterChip(
                                    selected = mood == value, onClick = { mood = value },
                                    label = { Text(label, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }

                    Column {
                        Text("\u5929\u6C14\u72B6\u51B5", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf("sunny" to "\u2600\uFE0F\u6674", "cloudy" to "\u26C5\u591A\u4E91",
                                "rainy" to "\uD83C\uDF27\uFE0F\u96E8", "snowy" to "\u2744\uFE0F\u96EA",
                                "stormy" to "\u26C8\uFE0F\u96F7\u96E8", "night" to "\uD83C\uDF19\u591C\u665A")) { (value, label) ->
                                FilterChip(
                                    selected = weather == value, onClick = { weather = value },
                                    label = { Text(label, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = content, onValueChange = { content = it },
                        label = { Text("\u8BB0\u5F55\u4ECA\u5929\u53D1\u751F\u7684\u4E8B\u60C5...") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp),
                        minLines = 8, maxLines = 20,
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = tags, onValueChange = { tags = it },
                        label = { Text("\u6807\u7B7E\uFF08\u9017\u53F7\u5206\u9694\uFF09") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) }
                    )

                    Column {
                        Text("\u6DFB\u52A0\u7167\u7247", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalButton(
                                onClick = { imagePicker.launch("image/*") },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("\u4ECE\u76F8\u518C\u9009\u62E9")
                            }
                            if (selectedImageUris.isNotEmpty()) {
                                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                    Text("\u5DF2\u9009${selectedImageUris.size}\u5F20", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryDetailDialog(
    entry: DiaryEntry,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("\uD83D\uDCD6 \u65E5\u8BB0\u8BE6\u60C5", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(entry.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MoodBadge(mood = entry.mood)
                        Spacer(modifier = Modifier.width(8.dp))
                        if (entry.weather.isNotBlank()) {
                            Text(
                                "${weatherEmoji(entry.weather)} ${
                                    when (entry.weather) {
                                        "sunny" -> "\u6674"; "cloudy" -> "\u591A\u4E91"; "rainy" -> "\u96E8"
                                        "snowy" -> "\u96EA"; "stormy" -> "\u96F7\u96E8"; "night" -> "\u591C\u665A"
                                        else -> entry.weather
                                    }
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(formatTimestampFull(entry.timestamp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    if (entry.tags.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("\uD83C\uDFF7\uFE0F ${entry.tags}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(entry.content, style = MaterialTheme.typography.bodyLarge)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDelete) { Text("\u5220\u9664", color = MaterialTheme.colorScheme.error) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onEdit, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("\u7F16\u8F91")
                    }
                }
            }
        }
    }
}
