package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@Composable
fun FileManagerScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mediaList by viewModel.mediaList.collectAsState()
    val storageStats by viewModel.storageStats.collectAsState()
    var selectedCategory by remember { mutableStateOf("All Files") }
    var selectedItemForInfo by remember { mutableStateOf<MediaItem?>(null) }
    var isCleaning by remember { mutableStateOf(false) }

    val filteredList = remember(mediaList, selectedCategory) {
        when (selectedCategory) {
            "Videos" -> mediaList.filter { it.mediaType == MediaType.VIDEO }
            "Music" -> mediaList.filter { it.mediaType == MediaType.AUDIO }
            "Favorites" -> mediaList.filter { it.isFavorite }
            else -> mediaList
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OmniObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {

        // Title Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SMART FILE MANAGER",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Storage Analytics • Junk Cleaner • Local Media Organization",
                        color = OmniTextSecondary,
                        fontSize = 10.sp
                    )
                }

                Surface(color = OmniViolet.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "${mediaList.size} ITEMS",
                        color = OmniViolet,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Storage Analytics & 1-Tap Junk Cleaner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniCyan.copy(alpha = 0.3f))))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "DEVICE STORAGE", color = OmniTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${storageStats.usedGb.toInt()} GB Used / ${storageStats.totalGb.toInt()} GB Total", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }

                        if (storageStats.cacheJunkMb > 0f) {
                            Button(
                                onClick = {
                                    viewModel.cleanCache()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OmniEmerald, contentColor = OmniObsidian),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("clean_cache_button")
                            ) {
                                Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Clean ${storageStats.cacheJunkMb.toInt()}MB", fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        } else {
                            Surface(color = OmniEmerald.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = "Storage Optimized",
                                    color = OmniEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Segmented Storage Progress Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(OmniSurfaceCard)
                    ) {
                        Box(modifier = Modifier.weight(storageStats.videosGb).fillMaxHeight().background(OmniCyan))
                        Box(modifier = Modifier.weight(storageStats.audioGb).fillMaxHeight().background(OmniViolet))
                        Box(modifier = Modifier.weight(storageStats.downloadsGb).fillMaxHeight().background(OmniEmerald))
                        Box(modifier = Modifier.weight(storageStats.freeGb).fillMaxHeight().background(OmniSurfaceCard))
                    }

                    // Storage Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LegendItem(color = OmniCyan, label = "Videos: ${storageStats.videosGb.toInt()}GB")
                        LegendItem(color = OmniViolet, label = "Music: ${storageStats.audioGb.toInt()}GB")
                        LegendItem(color = OmniEmerald, label = "Downloads: ${storageStats.downloadsGb.toInt()}GB")
                        LegendItem(color = OmniTextMuted, label = "Free: ${storageStats.freeGb.toInt()}GB")
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All Files", "Videos", "Music", "Favorites").forEach { cat ->
                    val isSel = selectedCategory == cat
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OmniCyan,
                            selectedLabelColor = OmniObsidian,
                            containerColor = OmniSurfaceElevated,
                            labelColor = OmniTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSel,
                            borderColor = if (isSel) OmniCyan else OmniBorder
                        )
                    )
                }
            }
        }

        // Filtered Files List
        items(filteredList) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (item.mediaType == MediaType.VIDEO) viewModel.playVideo(item) else viewModel.playTrack(item)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniBorder)))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (item.mediaType == MediaType.VIDEO) OmniCyan.copy(alpha = 0.15f) else OmniViolet.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.mediaType == MediaType.VIDEO) Icons.Default.Videocam else Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = if (item.mediaType == MediaType.VIDEO) OmniCyan else OmniViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${item.format} • ${(item.sizeBytes / 1000000)} MB • ${item.resolution}",
                                color = OmniTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Shared from OmniPlay Super App: ${item.title}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Media via")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = OmniCyan, modifier = Modifier.size(16.dp))
                        }

                        IconButton(
                            onClick = { selectedItemForInfo = item },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Details", tint = OmniTextSecondary, modifier = Modifier.size(16.dp))
                        }

                        IconButton(
                            onClick = { viewModel.deleteMedia(item.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = OmniRose, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    // --- MODAL: FILE PROPERTIES INFO ---
    if (selectedItemForInfo != null) {
        val item = selectedItemForInfo!!
        AlertDialog(
            onDismissRequest = { selectedItemForInfo = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = OmniCyan)
                    Text("File Properties", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    PropertyRow("Title", item.title)
                    PropertyRow("Type", item.mediaType.name)
                    PropertyRow("Format", item.format)
                    PropertyRow("Quality", item.resolution)
                    PropertyRow("Duration", "${(item.durationMs / 1000)} seconds")
                    PropertyRow("File Size", "${(item.sizeBytes / 1000000)} MB")
                    PropertyRow("Album/Artist", "${item.album} • ${item.artist}")
                    PropertyRow("Path", "/storage/emulated/0/OmniPlay/${item.format}/${item.id}")
                }
            },
            confirmButton = {
                Button(onClick = { selectedItemForInfo = null }, colors = ButtonDefaults.buttonColors(containerColor = OmniCyan, contentColor = OmniObsidian)) {
                    Text("Close", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = OmniSurfaceDark
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
        Text(text = label, color = OmniTextSecondary, fontSize = 9.sp)
    }
}

@Composable
fun PropertyRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = OmniTextMuted, fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
