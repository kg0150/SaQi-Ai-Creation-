package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadStatus
import com.example.data.model.DownloadTask
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@Composable
fun DownloaderScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val downloads by viewModel.downloads.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Active, 1: Completed

    var inputUrl by remember { mutableStateOf("https://archive.org/details/cinematic-travel-4k.mp4") }
    var inputTitle by remember { mutableStateOf("Pacific Rim Ocean Exploration 4K") }
    var selectedFormat by remember { mutableStateOf("MP4") }
    var selectedQuality by remember { mutableStateOf("1080p Full HD") }

    val activeDownloads = remember(downloads) {
        downloads.filter { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PAUSED || it.status == DownloadStatus.QUEUED }
    }
    val completedDownloads = remember(downloads) {
        downloads.filter { it.status == DownloadStatus.COMPLETED }
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
                        text = "ADVANCED DOWNLOADER",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "High-speed multi-threaded background download engine",
                        color = OmniTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    color = OmniCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${activeDownloads.size} ACTIVE",
                        color = OmniCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // URL Input & Quality Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniCyan.copy(alpha = 0.4f))))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "DOWNLOAD FROM AUTHORIZED SOURCE",
                        color = OmniCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        modifier = Modifier.fillMaxWidth().testTag("download_url_input"),
                        label = { Text("Media Source URL (Direct MP4, Audio, CC0)") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = OmniCyan) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OmniCyan,
                            unfocusedBorderColor = OmniBorder,
                            focusedContainerColor = OmniSurfaceElevated,
                            unfocusedContainerColor = OmniSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Custom File Title (Optional)") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = OmniViolet) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OmniViolet,
                            unfocusedBorderColor = OmniBorder,
                            focusedContainerColor = OmniSurfaceElevated,
                            unfocusedContainerColor = OmniSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Format Selection Chips
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Format", color = OmniTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("MP4", "MKV", "MP3", "M4A").forEach { fmt ->
                                val isSelected = selectedFormat == fmt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedFormat = fmt },
                                    label = { Text(text = fmt, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = OmniCyan,
                                        selectedLabelColor = OmniObsidian,
                                        containerColor = OmniSurfaceElevated,
                                        labelColor = OmniTextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) OmniCyan else OmniBorder
                                    )
                                )
                            }
                        }
                    }

                    // Quality Selection Chips
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Quality", color = OmniTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("1080p Full HD", "720p HD", "480p SD", "320kbps Audio").forEach { q ->
                                val isSelected = selectedQuality == q
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedQuality = q },
                                    label = { Text(text = q, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = OmniViolet,
                                        selectedLabelColor = Color.White,
                                        containerColor = OmniSurfaceElevated,
                                        labelColor = OmniTextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) OmniViolet else OmniBorder
                                    )
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (inputUrl.isNotBlank()) {
                                viewModel.submitDownload(inputUrl, inputTitle, selectedFormat, selectedQuality)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("start_download_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = OmniCyan, contentColor = OmniObsidian),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "START DOWNLOAD", fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }

                    // Security & Policy Notice
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = OmniEmerald, modifier = Modifier.size(14.dp))
                        Text(
                            text = "Downloads are restricted to authorized and public domain media.",
                            color = OmniTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Tabs: Active vs Completed
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = OmniSurfaceDark,
                contentColor = OmniCyan,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active Downloads (${activeDownloads.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Completed (${completedDownloads.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                )
            }
        }

        // List of Active Downloads
        if (selectedTab == 0) {
            if (activeDownloads.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.CloudDownload,
                        title = "No Active Downloads",
                        subtitle = "Paste a URL above to start downloading at high speed."
                    )
                }
            } else {
                items(activeDownloads) { task ->
                    ActiveDownloadCard(
                        task = task,
                        onPause = { viewModel.pauseDownload(task.id) },
                        onResume = { viewModel.resumeDownload(task.id) },
                        onCancel = { viewModel.cancelDownload(task.id) }
                    )
                }
            }
        } else {
            // Completed Downloads
            if (completedDownloads.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.FolderOpen,
                        title = "No Completed Downloads Yet",
                        subtitle = "Finished downloads will appear here with instant play options."
                    )
                }
            } else {
                items(completedDownloads) { task ->
                    CompletedDownloadCard(
                        task = task,
                        onDelete = { viewModel.cancelDownload(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveDownloadCard(
    task: DownloadTask,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    val animatedProgress by animateFloatAsState(targetValue = task.progress, label = "progress")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniBorder)))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${task.qualityLabel} • ${task.targetFormat}",
                        color = OmniTextSecondary,
                        fontSize = 10.sp
                    )
                }

                Surface(
                    color = if (task.status == DownloadStatus.DOWNLOADING) OmniCyan.copy(alpha = 0.2f) else OmniAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = task.status.name,
                        color = if (task.status == DownloadStatus.DOWNLOADING) OmniCyan else OmniAmber,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = OmniCyan,
                trackColor = OmniSurfaceCard
            )

            // Speed & ETA metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val speedMb = (task.speedBytesPerSec / 1000000f)
                val downloadedMb = (task.downloadedBytes / 1000000)
                val totalMb = (task.totalBytes / 1000000)

                Text(
                    text = "$downloadedMb MB / $totalMb MB • ${(task.progress * 100).toInt()}%",
                    color = OmniTextSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = if (task.status == DownloadStatus.DOWNLOADING) "${String.format("%.1f", speedMb)} MB/s • ETA ${task.etaSeconds}s" else "Paused",
                    color = OmniCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Controls: Pause / Resume / Cancel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.status == DownloadStatus.DOWNLOADING) {
                    IconButton(onClick = onPause, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause", tint = OmniAmber, modifier = Modifier.size(18.dp))
                    }
                } else {
                    IconButton(onClick = onResume, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = OmniCyan, modifier = Modifier.size(18.dp))
                    }
                }
                IconButton(onClick = onCancel, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = OmniRose, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CompletedDownloadCard(
    task: DownloadTask,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniBorder)))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(OmniEmerald.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OmniEmerald, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(text = task.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = "${task.qualityLabel} • ${(task.totalBytes / 1000000)} MB • ${task.targetFormat}", color = OmniTextSecondary, fontSize = 10.sp)
                    Text(text = "Saved in Downloads/OmniPlay", color = OmniTextMuted, fontSize = 9.sp)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = OmniTextMuted, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniBorder)))
    ) {
        Column(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = OmniTextMuted, modifier = Modifier.size(36.dp))
            Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = OmniTextSecondary, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
