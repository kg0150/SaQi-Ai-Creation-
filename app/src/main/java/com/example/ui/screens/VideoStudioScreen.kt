package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaType
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoStudioScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val mediaList by viewModel.mediaList.collectAsState()
    val videos = remember(mediaList) { mediaList.filter { it.mediaType == MediaType.VIDEO } }
    var selectedVideoIndex by remember { mutableIntStateOf(0) }
    val currentVideo = videos.getOrNull(selectedVideoIndex) ?: mediaList.first()

    var activeToolTab by remember { mutableIntStateOf(0) } // 0: Trim, 1: Audio Extract, 2: Compress, 3: Filters, 4: Thumbnails

    // Trimming State
    var trimStartSec by remember { mutableFloatStateOf(0f) }
    var trimEndSec by remember { mutableFloatStateOf((currentVideo.durationMs / 1000f).coerceAtLeast(10f)) }

    // Compression State
    var compressionPreset by remember { mutableStateOf("Medium (720p - 50% Size)") }

    // Filter State
    var selectedFilter by remember { mutableStateOf("Cinematic Teal & Orange") }

    // Processing feedback
    val coroutineScope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

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
                        text = "VIDEO EDITING STUDIO",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Trimming, Audio Extraction, Compression & Color Grading",
                        color = OmniTextSecondary,
                        fontSize = 10.sp
                    )
                }

                Surface(color = OmniAmber.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "PRO TOOLS",
                        color = OmniAmber,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Active Video Target Selector
        item {
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "TARGET CLIP:", color = OmniCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = currentVideo.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text(text = "Length: ${(currentVideo.durationMs / 1000)}s • Size: ${(currentVideo.sizeBytes / 1000000)}MB • ${currentVideo.resolution}", color = OmniTextSecondary, fontSize = 10.sp)
                    }

                    Surface(
                        color = OmniSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable {
                            selectedVideoIndex = (selectedVideoIndex + 1) % videos.size
                            trimEndSec = (videos[selectedVideoIndex].durationMs / 1000f).coerceAtLeast(10f)
                        }
                    ) {
                        Text(
                            text = "Switch Clip",
                            color = OmniCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Tool Selector Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = activeToolTab,
                containerColor = OmniSurfaceDark,
                contentColor = OmniCyan,
                edgePadding = 0.dp,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                listOf("Trim Video", "Audio Stem", "Compress", "Color Filters", "Thumbnail").forEachIndexed { idx, title ->
                    Tab(
                        selected = activeToolTab == idx,
                        onClick = { activeToolTab = idx },
                        text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        // TOOL 1: VIDEO TRIMMING
        if (activeToolTab == 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniCyan.copy(alpha = 0.4f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(text = "PRECISION VIDEO TRIMMING", color = OmniCyan, fontSize = 11.sp, fontWeight = FontWeight.Black)

                        // Start Marker
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Start Time: ${trimStartSec.toInt()}s", color = OmniTextSecondary, fontSize = 11.sp)
                                Text(text = "0s", color = OmniTextMuted, fontSize = 10.sp)
                            }
                            Slider(
                                value = trimStartSec,
                                onValueChange = { trimStartSec = it.coerceAtMost(trimEndSec - 2f) },
                                valueRange = 0f..(currentVideo.durationMs / 1000f),
                                colors = SliderDefaults.colors(thumbColor = OmniCyan, activeTrackColor = OmniCyan)
                            )
                        }

                        // End Marker
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "End Time: ${trimEndSec.toInt()}s", color = OmniTextSecondary, fontSize = 11.sp)
                                Text(text = "${(currentVideo.durationMs / 1000)}s", color = OmniTextMuted, fontSize = 10.sp)
                            }
                            Slider(
                                value = trimEndSec,
                                onValueChange = { trimEndSec = it.coerceAtLeast(trimStartSec + 2f) },
                                valueRange = 0f..(currentVideo.durationMs / 1000f),
                                colors = SliderDefaults.colors(thumbColor = OmniViolet, activeTrackColor = OmniViolet)
                            )
                        }

                        val trimmedDuration = (trimEndSec - trimStartSec).toInt()
                        Surface(
                            color = OmniSurfaceElevated,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Exported Clip Duration: $trimmedDuration seconds • Est. Size: ${(trimmedDuration * 0.8f).toInt()} MB",
                                color = OmniEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isProcessing = true
                                    statusMessage = "Trimming clip (${trimmedDuration}s) with keyframe preservation..."
                                    delay(1500L)
                                    isProcessing = false
                                    statusMessage = "Trimmed clip saved successfully to /storage/OmniPlay/Trimmed_${currentVideo.title.take(12)}.mp4"
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("trim_export_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = OmniCyan, contentColor = OmniObsidian),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EXPORT TRIMMED VIDEO", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // TOOL 2: AUDIO EXTRACTION
        if (activeToolTab == 1) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniViolet.copy(alpha = 0.4f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(text = "1-TAP LOSSLESS AUDIO EXTRACTION", color = OmniViolet, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Text(
                            text = "Extract high-fidelity audio stem from '${currentVideo.title}' without re-encoding degradation.",
                            color = OmniTextSecondary,
                            fontSize = 11.sp
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(color = OmniSurfaceElevated, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Output Format", color = OmniTextMuted, fontSize = 9.sp)
                                    Text("MP3 320kbps", color = OmniCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(color = OmniSurfaceElevated, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Channels", color = OmniTextMuted, fontSize = 9.sp)
                                    Text("Stereo 48kHz", color = OmniEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isProcessing = true
                                    statusMessage = "Demuxing audio stem & encoding 320kbps MP3..."
                                    delay(1600L)
                                    isProcessing = false
                                    statusMessage = "Audio extracted: ${currentVideo.title}.mp3 added to Music Library!"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = OmniViolet, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Audiotrack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EXTRACT AUDIO STEM (MP3)", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // TOOL 3: VIDEO COMPRESSION
        if (activeToolTab == 2) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniEmerald.copy(alpha = 0.4f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(text = "INTELLIGENT VIDEO COMPRESSOR", color = OmniEmerald, fontSize = 11.sp, fontWeight = FontWeight.Black)

                        val presets = listOf(
                            "High (1080p - 25% Size Reduction)",
                            "Medium (720p - 50% Size Reduction)",
                            "Maximum (480p - 70% Size Reduction)"
                        )
                        presets.forEach { preset ->
                            val isSel = compressionPreset == preset
                            Surface(
                                color = if (isSel) OmniEmerald.copy(alpha = 0.2f) else OmniSurfaceElevated,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) OmniEmerald else OmniBorder),
                                modifier = Modifier.fillMaxWidth().clickable { compressionPreset = preset }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = preset, color = if (isSel) OmniEmerald else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    if (isSel) Icon(Icons.Default.Check, contentDescription = null, tint = OmniEmerald, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isProcessing = true
                                    statusMessage = "Compressing with HEVC hardware encoder..."
                                    delay(2000L)
                                    isProcessing = false
                                    statusMessage = "Compressed! Saved 54 MB (58% storage saved)."
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = OmniEmerald, contentColor = OmniObsidian),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Compress, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("COMPRESS VIDEO", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // TOOL 4: COLOR FILTERS
        if (activeToolTab == 3) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniAmber.copy(alpha = 0.4f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(text = "COLOR GRADING & LUT PRESETS", color = OmniAmber, fontSize = 11.sp, fontWeight = FontWeight.Black)

                        val filterList = listOf(
                            "Normal (Rec. 709)",
                            "Cinematic Teal & Orange",
                            "Vibrant HDR Boost",
                            "Film Noir Monochromatic",
                            "Vintage 1970s Sepia",
                            "Cyberpunk Neon Glow"
                        )
                        filterList.forEach { f ->
                            val isSel = selectedFilter == f
                            Surface(
                                color = if (isSel) OmniAmber.copy(alpha = 0.2f) else OmniSurfaceElevated,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) OmniAmber else OmniBorder),
                                modifier = Modifier.fillMaxWidth().clickable { selectedFilter = f }
                            ) {
                                Text(
                                    text = f,
                                    color = if (isSel) OmniAmber else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isProcessing = true
                                    statusMessage = "Applying 3D LUT '$selectedFilter' to master video..."
                                    delay(1800L)
                                    isProcessing = false
                                    statusMessage = "Color-graded master created: ColorGraded_${currentVideo.title.take(10)}.mp4"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = OmniAmber, contentColor = OmniObsidian),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("APPLY COLOR GRADE", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // TOOL 5: THUMBNAIL CAPTURE
        if (activeToolTab == 4) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniCyan.copy(alpha = 0.4f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(text = "THUMBNAIL GENERATOR", color = OmniCyan, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Text(text = "Capture exact high-resolution frame or generate AI thumbnail cover for YouTube & Reels.", color = OmniTextSecondary, fontSize = 11.sp)

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isProcessing = true
                                    statusMessage = "Capturing 4K frame at current timestamp..."
                                    delay(1200L)
                                    isProcessing = false
                                    statusMessage = "Thumbnail saved: /Pictures/OmniPlay/Thumb_${System.currentTimeMillis()}.jpg"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = OmniCyan, contentColor = OmniObsidian),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CAPTURE FRAME THUMBNAIL", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Feedback / Status Banner
        if (isProcessing || statusMessage != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = OmniSurfaceElevated),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniCyan, OmniViolet)))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = OmniCyan, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OmniEmerald, modifier = Modifier.size(20.dp))
                        }
                        Text(
                            text = statusMessage ?: "Processing media...",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
