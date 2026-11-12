package com.example.ui.screens

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AdPlacement
import com.example.data.model.EqualizerPreset
import com.example.data.model.MediaType
import com.example.ui.components.MusicPlayerAdBar
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@Composable
fun MusicPlayerScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isMusicPlaying.collectAsState()
    val positionMs by viewModel.musicPositionMs.collectAsState()
    val isShuffle by viewModel.isShuffle.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val equalizerState by viewModel.equalizerState.collectAsState()
    val sleepTimerLeft by viewModel.sleepTimerMinutesLeft.collectAsState()
    val mediaList by viewModel.mediaList.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()

    var showEqualizerModal by remember { mutableStateOf(false) }
    var showSleepTimerModal by remember { mutableStateOf(false) }
    var showLyricsModal by remember { mutableStateOf(false) }

    val audioTracks = remember(mediaList) {
        mediaList.filter { it.mediaType == MediaType.AUDIO }
    }
    val musicAd = remember(campaigns) {
        campaigns.firstOrNull { it.placement == AdPlacement.MUSIC_PLAYER_BOTTOM && it.active }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OmniObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {

        // Top Navigation Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PRO MUSIC PLAYER",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Hi-Res Audio Engine • 5-Band Equalizer • 3D Spatial Sound",
                        color = OmniTextSecondary,
                        fontSize = 10.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { showEqualizerModal = true },
                        modifier = Modifier.testTag("open_equalizer_button")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Equalizer", tint = OmniCyan)
                    }
                    IconButton(onClick = { showSleepTimerModal = true }) {
                        Icon(Icons.Default.Timer, contentDescription = "Sleep Timer", tint = OmniViolet)
                    }
                }
            }
        }

        // Now Playing Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniViolet.copy(alpha = 0.4f))))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Album Art
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(OmniSurfaceCard)
                    ) {
                        AsyncImage(
                            model = currentTrack.thumbnailUrl,
                            contentDescription = currentTrack.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            color = Color.Black.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                        ) {
                            Text(
                                text = currentTrack.resolution,
                                color = OmniCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Track Details
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = currentTrack.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${currentTrack.artist} • ${currentTrack.album}",
                            color = OmniTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Real-Time Animated Waveform Spectrum
                    WaveformVisualizer(isPlaying = isPlaying)

                    // Scrubber Bar
                    val currentSec = (positionMs / 1000).toInt()
                    val totalSec = (currentTrack.durationMs / 1000).toInt().coerceAtLeast(1)
                    val progressFloat = (positionMs.toFloat() / currentTrack.durationMs.toFloat()).coerceIn(0f, 1f)

                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Slider(
                            value = progressFloat,
                            onValueChange = { frac ->
                                viewModel.seekMusic((frac * currentTrack.durationMs).toLong())
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = OmniCyan,
                                activeTrackColor = OmniCyan,
                                inactiveTrackColor = OmniSurfaceCard
                            ),
                            modifier = Modifier.fillMaxWidth().height(24.dp).testTag("music_scrubber")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = String.format("%02d:%02d", currentSec / 60, currentSec % 60),
                                color = OmniCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format("%02d:%02d", totalSec / 60, totalSec % 60),
                                color = OmniTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Primary Playback Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleShuffle() }) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = if (isShuffle) OmniCyan else OmniTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(onClick = { viewModel.previousTrack() }, modifier = Modifier.size(44.dp)) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Prev", tint = Color.White, modifier = Modifier.size(28.dp))
                        }

                        // Big Play / Pause Button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(OmniCyan, OmniViolet)))
                                .clickable { viewModel.toggleMusicPlayPause() }
                                .testTag("music_play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = OmniObsidian,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(onClick = { viewModel.nextTrack() }, modifier = Modifier.size(44.dp)) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(28.dp))
                        }

                        IconButton(onClick = { viewModel.toggleRepeat() }) {
                            Icon(
                                imageVector = when (repeatMode) {
                                    1 -> Icons.Default.Repeat
                                    2 -> Icons.Default.RepeatOne
                                    else -> Icons.Default.Repeat
                                },
                                contentDescription = "Repeat",
                                tint = if (repeatMode > 0) OmniCyan else OmniTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Action Pills: Favorite, Equalizer, Lyrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { viewModel.toggleFavorite(currentTrack.id) }) {
                            Icon(
                                imageVector = if (currentTrack.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (currentTrack.isFavorite) OmniRose else OmniTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Favorite", fontSize = 10.sp, color = OmniTextSecondary)
                        }

                        TextButton(onClick = { showEqualizerModal = true }) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = equalizerState.preset.displayName, fontSize = 10.sp, color = OmniCyan)
                        }

                        TextButton(onClick = { showLyricsModal = true }) {
                            Icon(Icons.Default.Subtitles, contentDescription = null, tint = OmniViolet, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Lyrics", fontSize = 10.sp, color = OmniViolet)
                        }
                    }
                }
            }
        }

        // Music Player Non-Intrusive Sponsor Bar (Zero audio interruption)
        item {
            MusicPlayerAdBar(
                campaign = musicAd,
                onImpression = { viewModel.onAdImpression(it) },
                onClick = { viewModel.onAdClick(it) }
            )
        }

        // Playlist / Local Music Library
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "AUDIO TRACKS (${audioTracks.size})",
                    color = OmniTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                audioTracks.forEach { track ->
                    val isCurrent = track.id == currentTrack.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.playTrack(track) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isCurrent) OmniSurfaceElevated else OmniSurfaceDark),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                if (isCurrent) listOf(OmniCyan, OmniViolet) else listOf(OmniBorder, OmniBorder)
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(OmniSurfaceCard),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCurrent && isPlaying) {
                                        Icon(Icons.Default.Equalizer, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(20.dp))
                                    } else {
                                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = OmniTextMuted, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Column {
                                    Text(
                                        text = track.title,
                                        color = if (isCurrent) OmniCyan else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${track.artist} • ${track.format} • ${track.resolution}",
                                        color = OmniTextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.toggleFavorite(track.id) }) {
                                Icon(
                                    imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (track.isFavorite) OmniRose else OmniTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- MODAL: 5-BAND GRAPHIC EQUALIZER ---
    if (showEqualizerModal) {
        AlertDialog(
            onDismissRequest = { showEqualizerModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = OmniCyan)
                    Text("5-Band Graphic Equalizer", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    // Preset Selection Chips
                    Text("Presets", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OmniTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            EqualizerPreset.BASS_BOOST,
                            EqualizerPreset.ROCK,
                            EqualizerPreset.POP,
                            EqualizerPreset.VOCAL
                        ).forEach { preset ->
                            val isSel = equalizerState.preset == preset
                            Surface(
                                color = if (isSel) OmniCyan else OmniSurfaceCard,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { viewModel.setEqualizerPreset(preset) }
                            ) {
                                Text(
                                    text = preset.displayName,
                                    color = if (isSel) OmniObsidian else OmniTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // 5-Band Sliders: 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz
                    val labels = listOf("60Hz", "230Hz", "910Hz", "3.6k", "14k")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        equalizerState.bands.forEachIndexed { idx, gain ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = labels.getOrElse(idx) { "Band $idx" },
                                    fontSize = 10.sp,
                                    color = OmniTextSecondary,
                                    modifier = Modifier.width(36.dp)
                                )
                                Slider(
                                    value = gain,
                                    onValueChange = { newVal -> viewModel.updateEqualizerBand(idx, newVal) },
                                    valueRange = -12f..12f,
                                    modifier = Modifier.weight(1f).height(20.dp),
                                    colors = SliderDefaults.colors(thumbColor = OmniCyan, activeTrackColor = OmniCyan)
                                )
                                Text(
                                    text = "${if (gain > 0) "+" else ""}${gain.toInt()}dB",
                                    fontSize = 10.sp,
                                    color = OmniCyan,
                                    modifier = Modifier.width(36.dp)
                                )
                            }
                        }
                    }

                    // Bass Boost & 3D Virtualizer Dials
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bass Boost: ${equalizerState.bassBoost.toInt()}%", fontSize = 10.sp, color = OmniViolet, fontWeight = FontWeight.Bold)
                            Slider(
                                value = equalizerState.bassBoost,
                                onValueChange = { viewModel.updateBassBoost(it) },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(thumbColor = OmniViolet, activeTrackColor = OmniViolet)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("3D Spatial: ${equalizerState.virtualizer3D.toInt()}%", fontSize = 10.sp, color = OmniEmerald, fontWeight = FontWeight.Bold)
                            Slider(
                                value = equalizerState.virtualizer3D,
                                onValueChange = { viewModel.updateVirtualizer(it) },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(thumbColor = OmniEmerald, activeTrackColor = OmniEmerald)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showEqualizerModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = OmniCyan, contentColor = OmniObsidian)
                ) {
                    Text("Apply & Close", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = OmniSurfaceDark
        )
    }

    // --- MODAL: SLEEP TIMER ---
    if (showSleepTimerModal) {
        AlertDialog(
            onDismissRequest = { showSleepTimerModal = false },
            title = { Text("Sleep Timer", fontSize = 15.sp, fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Automatically pause audio playback after timer expires.", fontSize = 11.sp, color = OmniTextSecondary)
                    listOf(15, 30, 45, 60, 0).forEach { mins ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSleepTimer(mins)
                                    showSleepTimerModal = false
                                },
                            colors = CardDefaults.cardColors(containerColor = OmniSurfaceElevated)
                        ) {
                            Text(
                                text = if (mins == 0) "Turn Off Timer" else "$mins Minutes",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (mins == 0) OmniRose else Color.White,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSleepTimerModal = false }) { Text("Close", color = OmniCyan) }
            },
            containerColor = OmniSurfaceDark
        )
    }

    // --- MODAL: LYRICS VIEWER ---
    if (showLyricsModal) {
        AlertDialog(
            onDismissRequest = { showLyricsModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = OmniViolet)
                    Text("Lyrics: ${currentTrack.title}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Surface(
                    color = OmniSurfaceCard,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)
                ) {
                    Text(
                        text = currentTrack.lyrics.ifBlank { "No synchronized lyrics embedded for this track." },
                        color = OmniCyan,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showLyricsModal = false }, colors = ButtonDefaults.buttonColors(containerColor = OmniViolet)) {
                    Text("Close", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = OmniSurfaceDark
        )
    }
}
