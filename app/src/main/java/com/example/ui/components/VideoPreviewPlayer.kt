package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SceneItem
import com.example.data.model.VideoFormat
import com.example.data.model.VideoProject

@Composable
fun VideoPreviewPlayer(
    project: VideoProject,
    isPlaying: Boolean,
    currentTimeSeconds: Float,
    selectedSceneIndex: Int,
    selectedAspectRatio: VideoFormat,
    onTogglePlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onSelectAspectRatio: (VideoFormat) -> Unit,
    onSelectSceneIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentScene: SceneItem? = project.scenes.getOrNull(selectedSceneIndex)
    val totalDuration = project.durationSeconds.coerceAtLeast(1f)

    // Calculate progress within the current scene (0f..1f)
    var sceneElapsed = 0f
    for (i in 0 until selectedSceneIndex) {
        sceneElapsed += project.scenes.getOrNull(i)?.durationSeconds ?: 0f
    }
    val currentSceneDuration = currentScene?.durationSeconds ?: 4f
    val sceneProgressFraction = ((currentTimeSeconds - sceneElapsed) / currentSceneDuration).coerceIn(0f, 1f)

    // Animated zoom pulse simulation for non-map scenes (Ken Burns effect)
    val infiniteTransition = rememberInfiniteTransition(label = "playerEffects")
    val ambientScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F1422))
            .border(1.dp, Color(0xFF26334D), RoundedCornerShape(20.dp))
            .padding(14.dp)
            .testTag("video_preview_player_card")
    ) {
        // Top Bar: Aspect Ratio Switcher and Resolution Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VideoFormat.values().forEach { fmt ->
                    val isSelected = selectedAspectRatio == fmt
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFFF6AD55) else Color(0xFF1E283F),
                        contentColor = if (isSelected) Color(0xFF0B0F19) else Color(0xFF94A3B8),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectAspectRatio(fmt) }
                            .testTag("aspect_ratio_${fmt.badge}")
                    ) {
                        Text(
                            text = fmt.badge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0x3310B981),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6610B981))
            ) {
                Text(
                    text = "${project.resolution.label.take(4)} • AI CLOUD RENDERED",
                    color = Color(0xFF34D399),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Player Viewport Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    when (selectedAspectRatio) {
                        VideoFormat.PORTRAIT_9_16 -> 320.dp
                        VideoFormat.LANDSCAPE_16_9 -> 200.dp
                        VideoFormat.SQUARE_1_1 -> 240.dp
                    }
                )
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black)
                .border(1.dp, Color(0xFF1E283F), RoundedCornerShape(14.dp))
                .testTag("video_player_viewport"),
            contentAlignment = Alignment.Center
        ) {
            if (currentScene?.isMapScene == true) {
                // Interactive 3D / 2D Map Canvas
                AnimatedMapCanvas(
                    modifier = Modifier.fillMaxSize(),
                    originLabel = "${project.mapConfig.originCountry} (${project.mapConfig.originCity})",
                    destLabel = "${project.mapConfig.destinationCountry} (${project.mapConfig.destinationCity})",
                    mapStyle = project.mapConfig.style,
                    progressFraction = if (isPlaying) sceneProgressFraction else null
                )
            } else {
                // Cinematic Photo/Video Visual Simulation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A),
                                    Color(0xFF020617)
                                )
                            )
                        )
                ) {
                    // Decorative visual frame elements
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when (selectedSceneIndex) {
                                0 -> Icons.Default.FlightTakeoff
                                2 -> Icons.Default.Terrain
                                3 -> Icons.Default.LocationCity
                                else -> Icons.Default.MovieFilter
                            },
                            contentDescription = "Scene visual placeholder",
                            tint = Color(0x66F6AD55),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentScene?.visualDescription ?: "Cinematic Scene Visual",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 3,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }

            // Top Status Badge on Video
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .background(Color(0xD90B0F19), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(if (isPlaying) Color(0xFFEF4444) else Color(0xFFF59E0B), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SCENE ${selectedSceneIndex + 1}/${project.scenes.size} • ${currentScene?.cameraMovement ?: "Cam"}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Real-Time Subtitle Overlay (Safe-Area compliant)
            currentScene?.subtitleText?.let { sub ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 14.dp, start = 16.dp, end = 16.dp)
                        .background(Color(0xEB000000), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = sub,
                        color = Color(0xFFFEF08A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Scrubber / Timeline Slider with scene markers
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = currentTimeSeconds,
                onValueChange = onSeek,
                valueRange = 0f..totalDuration,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFF6AD55),
                    activeTrackColor = Color(0xFFF6AD55),
                    inactiveTrackColor = Color(0xFF26334D)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .testTag("video_timeline_scrubber")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentMin = (currentTimeSeconds / 60).toInt()
                val currentSec = (currentTimeSeconds % 60).toInt()
                val totalMin = (totalDuration / 60).toInt()
                val totalSec = (totalDuration % 60).toInt()

                Text(
                    text = String.format("%02d:%02d", currentMin, currentSec),
                    color = Color(0xFFF6AD55),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${currentScene?.soundEffect ?: "Audio Stem"} • Ducking -18dB",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp
                )
                Text(
                    text = String.format("%02d:%02d", totalMin, totalSec),
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Playback Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Scene Quick Selectors
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                project.scenes.forEachIndexed { idx, sc ->
                    val isCurrent = idx == selectedSceneIndex
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCurrent) Color(0xFF38BDF8) else Color(0xFF1E283F),
                        contentColor = if (isCurrent) Color(0xFF0B0F19) else Color(0xFF94A3B8),
                        modifier = Modifier
                            .clickable { onSelectSceneIndex(idx) }
                            .testTag("scene_pill_$idx")
                    ) {
                        Text(
                            text = "S${idx + 1}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Central Transport Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onSeek((currentTimeSeconds - 5f).coerceAtLeast(0f)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay5,
                        contentDescription = "Replay 5s",
                        tint = Color(0xFF94A3B8)
                    )
                }

                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFFF6AD55), CircleShape)
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color(0xFF0B0F19),
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(
                    onClick = { onSeek((currentTimeSeconds + 5f).coerceAtMost(totalDuration)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward5,
                        contentDescription = "Forward 5s",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
