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
                entries.add(DiaryEntry(id = (entries.size + 1).toLong(), title = title, content = content,
                    mood = mood, timestamp = System.currentTimeMillis(), locationName = location?.first,
                    latitude = location?.second, longitude = location?.third, mediaPaths = mediaPaths))
                showAddDialog = false
            })
    }
    selectedEntry?.let { entry -> DiaryDetailDialog(entry = entry, onDismiss = { selectedEntry = null }) }
}

@Composable
fun DiaryEntryCard(entry: DiaryEntry, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(entry.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                MoodBadge(mood = entry.mood)
            }
            Spacer(Modifier.height(10.dp))
            Text(entry.content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 4)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.width(4.dp))
                    Text(formatTimestampFull(entry.timestamp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                if (entry.locationName != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(4.dp))
                        Text(entry.locationName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryDetailDialog(entry: DiaryEntry, onDismiss: () -> Unit) {
    var showMapDialog by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableIntStateOf(-1) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { Text("Close", color = MaterialTheme.colorScheme.onPrimaryContainer) }
                    Text(entry.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.width(80.dp))
                }
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MoodBadge(mood = entry.mood)
                        Text(formatTimestampFull(entry.timestamp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    if (entry.locationName != null && entry.latitude != null && entry.longitude != null) {
                        Card(modifier = Modifier.fillMaxWidth().clickable { showMapDialog = true },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.3f)),
                            shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(entry.locationName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    }
                                    TextButton(onClick = { showMapDialog = true }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)) {
                                        Icon(Icons.Default.Map, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Map", fontSize = 13.sp)
                                    }
                                }
                                Text("Lat: " + "%.4f".format(entry.latitude) + ", Lng: " + "%.4f".format(entry.longitude),
                                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Text(entry.content, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                    if (!entry.mediaPaths.isNullOrBlank()) {
                        try {
                            val imageUrls = parseMediaPaths(entry.mediaPaths).filter { MediaUtils.isFileAccessible(it) }
                            if (imageUrls.isNotEmpty()) {
                                Text("Photos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(imageUrls.size) { index ->
                                        Card(modifier = Modifier.size(100.dp).clickable { selectedImageIndex = index },
                                            shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                                            Image(painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current)
                                                .data(MediaUtils.pathToLoadableUri(imageUrls[index])).crossfade(true).build()),
                                                contentDescription = "Photo " + (index + 1), modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                        }
                                    }
                                }
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }
    if (showMapDialog) {
        LocationMapDialog(latitude = entry.latitude ?: 0.0, longitude = entry.longitude ?: 0.0,
            locationName = entry.locationName ?: "Unknown", onDismiss = { showMapDialog = false })
    }
    if (selectedImageIndex >= 0) {
        try {
            val urls = parseMediaPaths(entry.mediaPaths).filter { MediaUtils.isFileAccessible(it) }
            if (selectedImageIndex < urls.size) { ImagePreviewDialog(imageUrl = urls[selectedImageIndex], onDismiss = { selectedImageIndex = -1 }) }
        } catch (_: Exception) { selectedImageIndex = -1 }
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
                        loadUrl("https://www.openstreetmap.org/?mlat=" + latitude + "&mlon=" + longitude + "#map=15/" + latitude + "/" + longitude)
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
                Image(painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current)
                    .data(MediaUtils.pathToLoadableUri(imageUrl)).crossfade(true).build()),
                    contentDescription = "Preview", modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp), contentScale = ContentScale.Fit)
            }
        }
    }
}

fun parseMediaPaths(mediaPaths: String): List<String> {
    return try {
        val gson = com.google.gson.Gson()
        val type = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
        gson.fromJson<List<String>>(mediaPaths, type) ?: emptyList()
    } catch (_: Exception) {
        if (mediaPaths.isNotBlank() && !mediaPaths.startsWith("[")) listOf(mediaPaths) else emptyList()
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
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var locationName by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uris -> selectedImageUris = uris }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = MaterialTheme.colorScheme.onPrimaryContainer) }
                    Text("Write Diary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    TextButton(onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            val location = if (locationName.isNotBlank()) Triple(locationName, latitude, longitude) else null
                            val pathsJson = if (selectedImageUris.isNotEmpty()) MediaUtils.copyUrisToInternalStorage(context, selectedImageUris, "diary") else ""
                            onSave(title, content, mood, weather, tags, location, pathsJson)
                        }
                    }, enabled = title.isNotBlank() && content.isNotBlank()) {
                        Text("Save", fontWeight = FontWeight.Bold,
                            color = if (title.isNotBlank() && content.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                    }
                }
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Medium), shape = RoundedCornerShape(12.dp))

                    Column {
                        Text("Mood", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf(Pair("happy","\uD83D\uDE0A Happy"), Pair("sad","\uD83D\uDE22 Sad"),
                                Pair("neutral","\uD83D\uDE10 Calm"), Pair("excited","\uD83E\uDD29 Excited"),
                                Pair("anxious","\uD83D\uDE30 Anxious"), Pair("angry","\uD83D\uDE20 Angry"))) { (v, l) ->
                                FilterChip(selected = mood == v, onClick = { mood = v }, label = { Text(l, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer))
                            }
                        }
                    }

                    Column {
                        Text("Weather", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf(Pair("sunny","\u2600\uFE0F Sunny"), Pair("cloudy","\u26C5 Cloudy"),
                                Pair("rainy","\uD83C\uDF27\uFE0F Rainy"), Pair("snowy","\u2744\uFE0F Snowy"),
                                Pair("stormy","\u26C8\uFE0F Stormy"), Pair("night","\uD83C\uDF19 Night"))) { (v, l) ->
                                FilterChip(selected = weather == v, onClick = { weather = v }, label = { Text(l, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer))
                            }
                        }
                    }

                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("What happened today...") },
                        modifier = Modifier.fillMaxWidth().heightIn(160.dp), minLines = 8, maxLines = 20, shape = RoundedCornerShape(12.dp))

                    OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (comma separated)") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Tag, null) })

                    OutlinedTextField(value = locationName, onValueChange = { locationName = it }, label = { Text("Location (optional)") },
                        placeholder = { Text("e.g., Central Park") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), leadingIcon = { Icon(Icons.Default.LocationOn, null) })

                    Column {
                        Text("Add Photos", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalButton(onClick = { imagePicker.launch("image/*") }, shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Default.PhotoLibrary, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("From Album")
                            }
                            if (selectedImageUris.isNotEmpty()) {
                                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                    Text("Selected " + selectedImageUris.size + " photos", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                        if (selectedImageUris.isNotEmpty()) {
                            LazyRow(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(selectedImageUris.size) { idx ->
                                    Card(modifier = Modifier.size(80.dp), shape = RoundedCornerShape(8.dp)) {
                                        Image(painter = rememberAsyncImagePainter(ImageRequest.Builder(context)
                                            .data(selectedImageUris[idx]).crossfade(true).build()),
                                            contentDescription = "Selected " + (idx + 1), modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
