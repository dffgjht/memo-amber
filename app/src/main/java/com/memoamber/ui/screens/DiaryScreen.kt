package com.memoamber.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.memoamber.data.MemoAmberDatabase
import com.memoamber.data.entities.DiaryEntry
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

    val entriesFlow = remember { MutableStateFlow<List<DiaryEntry>>(emptyList()) }
    val entries by entriesFlow.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    fun loadEntries() {
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val dao = MemoAmberDatabase.getDatabase(context).diaryEntryDao()
                    entriesFlow.value = dao.getAllEntriesSync()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadEntries()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("日记", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        editingEntry = null
                        showEditDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "添加日记")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingEntry = null
                    showEditDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Edit, contentDescription = "写日记")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("还没有日记", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("点击 + 记录今天的心情", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    DiaryEntryCard(
                        entry = entry,
                        onClick = { selectedEntry = entry }
                    )
                }
            }
        }
    }

    // 新建 / 编辑对话框
    if (showEditDialog) {
        DiaryEditDialog(
            initial = editingEntry,
            onDismiss = { showEditDialog = false },
            onSave = { entry ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val dao = MemoAmberDatabase.getDatabase(context).diaryEntryDao()
                            if (entry.id > 0) {
                                dao.updateEntry(entry)
                            } else {
                                dao.insertEntry(entry)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    loadEntries()
                    showEditDialog = false
                }
            }
        )
    }

    // 详情对话框
    selectedEntry?.let { entry ->
        DiaryDetailDialog(
            entry = entry,
            onDismiss = { selectedEntry = null },
            onEdit = {
                selectedEntry = null
                editingEntry = entry
                showEditDialog = true
            },
            onDelete = {
                selectedEntry = null
                pendingDelete = entry
            }
        )
    }

    // 删除确认
    pendingDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("删除日记") },
            text = { Text("确定要删除「${entry.title}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            try {
                                val dao = MemoAmberDatabase.getDatabase(context).diaryEntryDao()
                                dao.deleteEntryById(entry.id)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        loadEntries()
                        pendingDelete = null
                    }
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun DiaryEntryCard(entry: DiaryEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                MoodBadge(mood = entry.mood)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatTimestampFull(entry.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                if (entry.weather.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = weatherEmoji(entry.weather),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoodBadge(mood: String) {
    val (emoji, label) = moodLabel(mood)
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "$emoji $label",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

/** 心情 → 文案映射 */
fun moodLabel(mood: String): Pair<String, String> = when (mood) {
    "happy" -> "😊" to "开心"
    "sad" -> "😢" to "难过"
    "neutral" -> "😐" to "平静"
    "excited" -> "🤩" to "兴奋"
    "anxious" -> "😰" to "焦虑"
    "angry" -> "😠" to "生气"
    else -> "😐" to "平静"
}

/** 天气 → emoji 映射 */
fun weatherEmoji(weather: String): String = when (weather) {
    "sunny" -> "☀️"
    "cloudy" -> "⛅"
    "rainy" -> "🌧️"
    "snowy" -> "❄️"
    "stormy" -> "⛈️"
    "night" -> "🌙"
    else -> ""
}

/** 新建 / 编辑日记对话框（编辑时传入 initial） */
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
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImageUris = uris
    }

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
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Text(
                        if (initial == null) "写日记" else "编辑日记",
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
                                        mediaPaths = if (selectedImageUris.isEmpty()) {
                                            initial?.mediaPaths ?: ""
                                        } else {
                                            Gson().toJson(selectedImageUris.map { it.toString() })
                                        }
                                    )
                                )
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank()
                    ) {
                        Text("保存",
                            fontWeight = FontWeight.Bold,
                            color = if (title.isNotBlank() && content.isNotBlank())
                                MaterialTheme.colorScheme.primary
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
                    // 标题
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("标题") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 心情选择
                    Column {
                        Text("今天心情", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf(
                                "happy" to "😊开心", "sad" to "😢难过",
                                "neutral" to "😐平静", "excited" to "🤩兴奋",
                                "anxious" to "😰焦虑", "angry" to "😠生气"
                            )) { (value, label) ->
                                FilterChip(
                                    selected = mood == value,
                                    onClick = { mood = value },
                                    label = { Text(label, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }

                    // 天气选择
                    Column {
                        Text("天气状况", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf(
                                "sunny" to "☀️晴", "cloudy" to "⛅多云",
                                "rainy" to "🌧️雨", "snowy" to "❄️雪",
                                "stormy" to "⛈️雷雨", "night" to "🌙夜晚"
                            )) { (value, label) ->
                                FilterChip(
                                    selected = weather == value,
                                    onClick = { weather = value },
                                    label = { Text(label, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                            }
                        }
                    }

                    // 内容
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("记录今天发生的事情...") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp),
                        minLines = 8,
                        maxLines = 20,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 标签
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("标签（多个标签用逗号分隔）") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) }
                    )

                    // 添加图片
                    Column {
                        Text("添加照片", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = { imagePicker.launch("image/*") },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null,
                                    modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("从相册选择")
                            }
                            if (selectedImageUris.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text("已选${selectedImageUris.size}张",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 日记详情对话框 */
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
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "日记详情",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "关闭",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MoodBadge(mood = entry.mood)
                        Spacer(modifier = Modifier.width(8.dp))
                        if (entry.weather.isNotBlank()) {
                            Text(
                                text = "${weatherEmoji(entry.weather)} ${
                                    when (entry.weather) {
                                        "sunny" -> "晴"; "cloudy" -> "多云"; "rainy" -> "雨"
                                        "snowy" -> "雪"; "stormy" -> "雷雨"; "night" -> "夜晚"
                                        else -> entry.weather
                                    }
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatTimestampFull(entry.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (entry.tags.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🏷️ ${entry.tags}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = entry.content,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Footer 操作按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDelete) {
                        Text("删除", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("编辑")
                    }
                }
            }
        }
    }
}
