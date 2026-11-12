package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AdPlacement
import com.example.data.model.AspectRatioMode
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.components.VideoPlayerAdPanel
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val currentVideo by viewModel.currentVideo.collectAsState()
    val isPlaying by viewModel.isVideoPlaying.collectAsState()
    val positionMs by viewModel.videoPositionMs.collectAsState()
    val speed by viewModel.videoSpeed.collectAsState()
    val subtitlesEnabled by viewModel.subtitlesEnabled.collectAsState()
    val aspectRatio by viewModel.aspectRatioMode.collectAsState()
    val isPipActive by viewModel.isPipActive.collectAsState()
    val brightness by viewModel.brightness.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val mediaList by viewModel.mediaList.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()

    var showControls by remember { mutableStateOf(true) }
    var hudText by remember { mutableStateOf<String?>(null) }
    var hudIcon by remember { mutableStateOf(Icons.Default.BrightnessMedium) }

    // Auto-hide controls timer
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4000L)
            showControls = false
        }
    }

    // Auto-hide gesture HUD
    LaunchedEffect(hudText) {
        if (hudText != null) {
            delay(1200L)
            hudText = null
        }
    }

    val videoList = remember(mediaList) {
        mediaList.filter { it.mediaType == MediaType.VIDEO }
    }
    val videoAd = remember(campaigns) {
        campaigns.firstOrNull { it.placement == AdPlacement.VIDEO_PLAYER_SIDE && it.active }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OmniObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {

        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ADVANCED VIDEO PLAYER",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "ExoPlayer Engine • Gesture HUD • Hardware Acceleration",
                        color = OmniTextSecondary,
                        fontSize = 10.sp
                    )
                }

                Surface(
                    color = OmniEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(OmniEmerald))
                        Text(text = "HW ACCEL ON", color = OmniEmerald, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Integrated Video Viewport with Gestures
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .testTag("video_player_viewport"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniCyan.copy(alpha = 0.3f))))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { showControls = !showControls },
                                onDoubleTap = { offset ->
                                    val isRight = offset.x > size.width / 2
                                    if (isRight) {
                                        viewModel.seekVideo(positionMs + 10000L)
                                        hudIcon = Icons.Default.Forward10
                                        hudText = "+10s"
                                    } else {
                                        viewModel.seekVideo(positionMs - 10000L)
                                        hudIcon = Icons.Default.Replay10
                                        hudText = "-10s"
                                    }
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val isRightSide = change.position.x > size.width / 2
                                if (isRightSide) {
                                    // Volume adjust
                                    val delta = -dragAmount.y / 200f
                                    viewModel.updateVolume(delta)
                                    hudIcon = Icons.Default.VolumeUp
                                    hudText = "Volume ${(volume * 100).toInt()}%"
                                } else {
                                    // Brightness adjust
                                    val delta = -dragAmount.y / 200f
                                    viewModel.updateBrightness(delta)
                                    hudIcon = Icons.Default.BrightnessMedium
                                    hudText = "Brightness ${(brightness * 100).toInt()}%"
                                }
                            }
                        }
                ) {

                    // Video Frame Background
                    AsyncImage(
                        model = currentVideo.thumbnailUrl,
                        contentDescription = currentVideo.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = when (aspectRatio) {
                            AspectRatioMode.FILL -> ContentScale.Crop
                            else -> ContentScale.Fit
                        }
                    )

                    // Subtitles Display Overlay
                    if (subtitlesEnabled && currentVideo.srtSubtitles.isNotEmpty()) {
                        val activeCue = currentVideo.srtSubtitles.find {
                            positionMs in it.startTimeMs..it.endTimeMs
                        }
                        if (activeCue != null) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = if (showControls) 54.dp else 16.dp, start = 16.dp, end = 16.dp)
                            ) {
                                Text(
                                    text = activeCue.text,
                                    color = OmniCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Gesture HUD Overlay (Brightness, Volume, Seek feedback)
                    if (hudText != null) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OmniCyan.copy(alpha = 0.5f)),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(hudIcon, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(20.dp))
                                Text(text = hudText ?: "", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // On-Screen Controls Overlay
                    if (showControls) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {

                            // Top Controls: Title + Format + PiP
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(color = OmniCyan.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = currentVideo.resolution,
                                            color = OmniCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = currentVideo.title,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 180.dp)
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    IconButton(onClick = { viewModel.toggleSubtitles() }, modifier = Modifier.size(30.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Subtitles,
                                            contentDescription = "Subtitles",
                                            tint = if (subtitlesEnabled) OmniCyan else OmniTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(onClick = { viewModel.togglePip() }, modifier = Modifier.size(30.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.PictureInPicture,
                                            contentDescription = "PiP",
                                            tint = if (isPipActive) OmniCyan else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // Center Play / Pause & Skip
                            Row(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { viewModel.seekVideo(positionMs - 10000L) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Replay10, contentDescription = "Back 10s", tint = Color.White, modifier = Modifier.size(28.dp))
                                }

                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(OmniCyan, OmniViolet)))
                                        .clickable { viewModel.toggleVideoPlayPause() }
                                        .testTag("video_play_pause_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = OmniObsidian,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.seekVideo(positionMs + 10000L) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = Color.White, modifier = Modifier.size(28.dp))
                                }
                            }

                            // Bottom Controls: Scrubber + Speeds + Aspect Ratio
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                val currentSec = (positionMs / 1000).toInt()
                                val totalSec = (currentVideo.durationMs / 1000).toInt().coerceAtLeast(1)
                                val progressFloat = (positionMs.toFloat() / currentVideo.durationMs.toFloat()).coerceIn(0f, 1f)

                                Slider(
                                    value = progressFloat,
                                    onValueChange = { frac ->
                                        viewModel.seekVideo((frac * currentVideo.durationMs).toLong())
                                    },
                                    colors = SliderDefaults.colors(thumbColor = OmniCyan, activeTrackColor = OmniCyan),
                                    modifier = Modifier.fillMaxWidth().height(16.dp).testTag("video_scrubber")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${String.format("%02d:%02d", currentSec / 60, currentSec % 60)} / ${String.format("%02d:%02d", totalSec / 60, totalSec % 60)}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        // Speed button
                                        Surface(
                                            color = OmniSurfaceElevated,
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.clickable {
                                                val nextSpeed = when (speed) {
                                                    1.0f -> 1.25f
                                                    1.25f -> 1.5f
                                                    1.5f -> 2.0f
                                                    2.0f -> 0.75f
                                                    else -> 1.0f
                                                }
                                                viewModel.setVideoSpeed(nextSpeed)
                                            }
                                        ) {
                                            Text(
                                                text = "${speed}x",
                                                color = OmniCyan,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }

                                        // Aspect ratio button
                                        Surface(
                                            color = OmniSurfaceElevated,
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.clickable {
                                                val nextRatio = when (aspectRatio) {
                                                    AspectRatioMode.FIT -> AspectRatioMode.FILL
                                                    AspectRatioMode.FILL -> AspectRatioMode.SIXTEEN_NINE
                                                    AspectRatioMode.SIXTEEN_NINE -> AspectRatioMode.FIT
                                                    else -> AspectRatioMode.FIT
                                                }
                                                viewModel.setAspectRatio(nextRatio)
                                            }
                                        ) {
                                            Text(
                                                text = aspectRatio.label,
                                                color = OmniViolet,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Non-Intrusive Video Player Sponsor Panel (Never interrupts video)
        item {
            VideoPlayerAdPanel(
                campaign = videoAd,
                onImpression = { viewModel.onAdImpression(it) },
                onClick = { viewModel.onAdClick(it) }
            )
        }

        // Categorized Video Library
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "VIDEO LIBRARY (${videoList.size})",
                    color = OmniTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                videoList.forEach { video ->
                    val isCurrent = video.id == currentVideo.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.playVideo(video) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isCurrent) OmniSurfaceElevated else OmniSurfaceDark),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                if (isCurrent) listOf(OmniCyan, OmniCyan) else listOf(OmniBorder, OmniBorder)
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.size(76.dp, 50.dp).clip(RoundedCornerShape(8.dp))) {
                                AsyncImage(
                                    model = video.thumbnailUrl,
                                    contentDescription = video.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                if (isCurrent && isPlaying) {
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = video.title,
                                    color = if (isCurrent) OmniCyan else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${video.artist} • ${video.resolution}",
                                    color = OmniTextSecondary,
                                    fontSize = 10.sp
                                )
                                if (video.lastPositionMs > 0L) {
                                    val sec = (video.lastPositionMs / 1000).toInt()
                                    Text(
                                        text = "Resume at ${String.format("%02d:%02d", sec / 60, sec % 60)}",
                                        color = OmniAmber,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.playVideo(video) }) {
                                Icon(Icons.Default.PlayCircleOutline, contentDescription = "Play", tint = OmniCyan, modifier = Modifier.size(26.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
