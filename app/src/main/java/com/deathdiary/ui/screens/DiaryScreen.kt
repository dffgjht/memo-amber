package com.deathdiary.ui.screens

import com.deathdiary.data.entities.DiaryEntry
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.deathdiary.utils.MediaUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(onNavigateBack: () -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    val context = LocalContext.current

    val entries = remember {
        mutableStateListOf(
            DiaryEntry(id = 1, title = "Beautiful Day", content = "Great weather today...", mood = "happy",
                timestamp = System.currentTimeMillis() - 86400000, locationName = "Central Park",
                latitude = 40.7829, longitude = -73.9654, mediaPaths = "[]"),
            DiaryEntry(id = 2, title = "Thinking About Life", content = "Recently been reflecting...",
                mood = "neutral", timestamp = System.currentTimeMillis() - 172800000)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Diary", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, "Add") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Edit, "Write")
            }
        }
    ) { pv ->
        androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.fillMaxSize().padding(pv),
            contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (entries.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Book, null, Modifier.size(80.dp), tint = MaterialTheme.colorScheme.outline.copy(0.5f))
                            Spacer(Modifier.height(16.dp))
                            Text("No diary entries yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            } else {
                androidx.compose.foundation.lazy.items(entries) { entry ->
                    DiaryEntryCard(entry = entry, onClick = { selectedEntry = entry })
                }
            }
        }
    }
    if (showAddDialog) {
        AddDiaryFullDialog(onDismiss = { showAddDialog = false },
            onSave = { title, content, mood, weather, tags, location, mediaPaths ->
                try {
                    entries.add(DiaryEntry(id = (entries.size + 1).toLong(), title = title, content = content,
                        mood = mood, timestamp = System.currentTimeMillis(), locationName = location?.first,
                        latitude = location?.second, longitude = location?.third, mediaPaths = mediaPaths))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                showAddDialog = false
            })
    }
    selectedEntry?.let { entry ->
        try {
            DiaryDetailDialog(entry = entry, onDismiss = { selectedEntry = null })
        } catch (e: Exception) {
            e.printStackTrace()
            selectedEntry = null
        }
    }
}

@Composable
fun DiaryEntryCard(entry: DiaryEntry, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(entry.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                MoodBadge(mood = entry.mood)
            }
            Spacer(Modifier.height(8.dp))
            Text(entry.content, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, maxLines = 2)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(TimeUtils.formatTime(entry.timestamp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (entry.latitude != null && entry.longitude != null) {
                        Surface(color = MaterialTheme.colorScheme.primaryContainer.copy(0.5f), shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(entry.locationName ?: "Location", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    if (!entry.mediaPaths.isNullOrBlank() && entry.mediaPaths != "[]") {
                        Surface(color = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f), shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Photos", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailDialog(entry: DiaryEntry, onDismiss: () -> Unit) {
    var selectedImageIndex by remember { mutableIntStateOf(-1) }
    var showMap by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val imageUrls = remember(entry.mediaPaths) {
        try {
            val paths = parseMediaPaths(entry.mediaPaths)
            paths.filter { MediaUtils.isFileAccessible(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f).verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(entry.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    MoodBadge(mood = entry.mood)
                    Text(TimeUtils.formatTime(entry.timestamp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                }
                Spacer(Modifier.height(20.dp))
                Text(entry.content, style = MaterialTheme.typography.bodyLarge)
                if (imageUrls.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    Text("Photos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(imageUrls.size) { index ->
                            Card(modifier = Modifier.size(120.dp, 120.dp).clickable { selectedImageIndex = index },
                                shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                                try {
                                    Image(painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current).data(MediaUtils.pathToLoadableUri(imageUrls[index])).crossfade(true).build()),
                                        contentDescription = "Photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                } catch (e: Exception) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.BrokenImage, null, tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
                if (entry.latitude != null && entry.longitude != null) {
                    Spacer(Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth().clickable { showMap = true }.height(200.dp),
                        shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.LocationOn, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(8.dp))
                                Text(entry.locationName ?: "View Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                                Text("Tap to open map", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
    if (selectedImageIndex >= 0 && selectedImageIndex < imageUrls.size) {
        try {
            ImagePreviewDialog(imageUrl = imageUrls[selectedImageIndex], onDismiss = { selectedImageIndex = -1 })
        } catch (_: Exception) {
            selectedImageIndex = -1
        }
    }
    if (showMap && entry.latitude != null && entry.longitude != null) {
        try {
            LocationMapDialog(latitude = entry.latitude, longitude = entry.longitude,
                locationName = entry.locationName ?: "Location", onDismiss = { showMap = false })
        } catch (e: Exception) {
            e.printStackTrace()
            showMap = false
        }
    }
}

@Composable
fun LocationMapDialog(latitude: Double, longitude: Double, locationName: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).height(420.dp), shape = RoundedCornerShape(16.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(locationName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }
                AndroidView(factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.setSupportZoom(true)
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        webViewClient = WebViewClient()
                        loadUrl("https://www.openstreetmap.org/?mlat=$latitude&mlon=$longitude#map=15/$latitude/$longitude")
                    }
                }, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun ImagePreviewDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), shape = RoundedCornerShape(16.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }
                try {
                    Image(painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(MediaUtils.pathToLoadableUri(imageUrl))
                            .crossfade(true)
                            .build()),
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp),
                        contentScale = ContentScale.Fit)
                } catch (e: Exception) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.BrokenImage, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Failed to load image", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

fun parseMediaPaths(mediaPaths: String?): List<String> {
    if (mediaPaths.isNullOrBlank()) return emptyList()
    return try {
        val gson = com.google.gson.Gson()
        val type = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
        gson.fromJson<List<String>>(mediaPaths, type) ?: emptyList()
    } catch (_: Exception) {
        if (mediaPaths.startsWith("[")) emptyList() else listOf(mediaPaths)
    }
}

@Composable
fun MoodBadge(mood: String) {
    val (emoji, label) = when (mood) {
        "happy" -> Pair("\uD83D\uDE0A", "Happy")
        "sad" -> Pair("\uD83D\uDE22", "Sad")
        "neutral" -> Pair("\uD83D\uDE10", "Calm")
        "excited" -> Pair("\uD83E\uDD29", "Excited")
        "anxious" -> Pair("\uD83D\uDE30", "Anxious")
        "angry" -> Pair("\uD83D\uDE20", "Angry")
        else -> Pair("\uD83D\uDE10", "Calm")
    }
    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(12.dp)) {
        Text(emoji + " " + label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDiaryFullDialog(onDismiss: () -> Unit, onSave: (String, String, String, String, String, Triple<String, Double, Double>?, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("neutral") }
    var weather by remember { mutableStateOf("sunny") }
    var tags by remember { mutableStateOf("") }

    val moodOptions = listOf("happy", "sad", "neutral", "excited", "anxious", "angry")
    val weatherOptions = listOf("sunny", "cloudy", "rainy", "snowy")

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f).verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("New Entry", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("What's on your mind?") },
                    modifier = Modifier.fillMaxWidth().height(150.dp), shape = RoundedCornerShape(12.dp))
                Spacer(Modifier.height(16.dp))
                Column {
                    Text("Mood", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(moodOptions.size) { idx ->
                            val m = moodOptions[idx]
                            FilterChip(selected = mood == m, onClick = { mood = m },
                                label = { Text(m.capitalize()) }, modifier = Modifier.height(36.dp))
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            onSave(title, content, mood, weather, tags, null, "[]")
                        }
                    }, modifier = Modifier.weight(1f), enabled = title.isNotBlank() && content.isNotBlank()) {
                        Text("Save Entry")
                    }
                }
            }
        }
    }
}
