package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AdPlacement
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.components.HomeAdBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniNavigationItem
import com.example.ui.viewmodel.OmniViewModel

@Composable
fun HomeScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val mediaList by viewModel.mediaList.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()
    val isMusicPlaying by viewModel.isMusicPlaying.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val currentVideo by viewModel.currentVideo.collectAsState()
    var searchInput by remember { mutableStateOf("") }

    val homeBanners = remember(campaigns) {
        campaigns.filter { it.placement == AdPlacement.HOME_BANNER && it.active }
    }
    val nativeAds = remember(campaigns) {
        campaigns.filter { (it.placement == AdPlacement.NATIVE_FEED || it.placement == AdPlacement.VIDEO_LIBRARY_CARD) && it.active }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OmniObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {

        // Top Brand Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(OmniCyan, OmniViolet))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Logo",
                            tint = OmniObsidian,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "OMNIPLAY",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                color = OmniCyan.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "FREE",
                                    color = OmniCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "All-In-One Multimedia Super App",
                            color = OmniTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Cloud / Admin badge
                Surface(
                    color = OmniSurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OmniBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(OmniEmerald))
                        Text(text = "Ad-Supported", color = OmniEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Search Bar with Smart Filter
        item {
            OutlinedTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_bar"),
                placeholder = {
                    Text(
                        text = "Search authorized videos, songs, or paste URL...",
                        fontSize = 12.sp,
                        color = OmniTextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = OmniCyan
                    )
                },
                trailingIcon = {
                    if (searchInput.isNotBlank()) {
                        IconButton(onClick = { searchInput = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = OmniTextMuted)
                        }
                    } else {
                        Surface(
                            color = OmniViolet.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { viewModel.navigateTo(OmniNavigationItem.AI_ASSISTANT) }
                        ) {
                            Text(
                                text = "AI Search",
                                color = OmniViolet,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OmniCyan,
                    unfocusedBorderColor = OmniBorder,
                    focusedContainerColor = OmniSurfaceDark,
                    unfocusedContainerColor = OmniSurfaceDark
                ),
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Live Advertising Banner
        item {
            HomeAdBanner(
                campaigns = homeBanners,
                onImpression = { viewModel.onAdImpression(it) },
                onClick = { viewModel.onAdClick(it) }
            )
        }

        // Quick Hub Grid (All 6 core modules)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "MULTIMEDIA SUITE",
                    color = OmniTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickModuleCard(
                        title = "Downloader",
                        subtitle = "Authorized 4K/MP3",
                        icon = Icons.Default.Download,
                        tint = OmniCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(OmniNavigationItem.DOWNLOADS) }
                    )
                    QuickModuleCard(
                        title = "Music Player",
                        subtitle = "5-Band Equalizer",
                        icon = Icons.Default.MusicNote,
                        tint = OmniViolet,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(OmniNavigationItem.MUSIC) }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickModuleCard(
                        title = "Video Player",
                        subtitle = "Gestures & PiP",
                        icon = Icons.Default.SmartDisplay,
                        tint = OmniEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(OmniNavigationItem.VIDEO) }
                    )
                    QuickModuleCard(
                        title = "Video Studio",
                        subtitle = "Trim, Merge & Stems",
                        icon = Icons.Default.MovieFilter,
                        tint = OmniAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(OmniNavigationItem.STUDIO) }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickModuleCard(
                        title = "AI Assistant",
                        subtitle = "Summaries & Subs",
                        icon = Icons.Default.AutoAwesome,
                        tint = OmniCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(OmniNavigationItem.AI_ASSISTANT) }
                    )
                    QuickModuleCard(
                        title = "File Manager",
                        subtitle = "Storage & Cleaner",
                        icon = Icons.Default.Folder,
                        tint = OmniViolet,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(OmniNavigationItem.FILES) }
                    )
                }
            }
        }

        // Active Playback Quick Resume Card
        if (isMusicPlaying) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(OmniNavigationItem.MUSIC) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = OmniSurfaceElevated),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniViolet, OmniCyan)))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(OmniViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(text = "NOW PLAYING", color = OmniCyan, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                Text(text = currentTrack.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(text = currentTrack.artist, color = OmniTextSecondary, fontSize = 10.sp)
                            }
                        }

                        IconButton(onClick = { viewModel.toggleMusicPlayPause() }) {
                            Icon(Icons.Default.PauseCircle, contentDescription = "Pause", tint = OmniCyan, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }
        }

        // Trending Authorized Videos Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRENDING 4K VIDEOS",
                        color = OmniTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "View All",
                        color = OmniCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.navigateTo(OmniNavigationItem.VIDEO) }
                    )
                }

                val videos = mediaList.filter { it.mediaType == MediaType.VIDEO }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(videos) { video ->
                        TrendingVideoCard(
                            video = video,
                            onClick = { viewModel.playVideo(video) },
                            onDownload = {
                                viewModel.submitDownload(video.uri, video.title, video.format, video.resolution)
                            }
                        )
                    }
                }
            }
        }

        // Native Sponsored Card In Feed
        if (nativeAds.isNotEmpty()) {
            val nativeAd = nativeAds.first()
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onAdClick(nativeAd.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniAmber.copy(alpha = 0.4f))))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = nativeAd.imageUrl,
                            contentDescription = "Sponsor preview",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(OmniSurfaceCard),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = OmniAmber.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "SPONSORED",
                                        color = OmniAmber,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Text(text = nativeAd.brandName, color = OmniTextSecondary, fontSize = 10.sp)
                            }
                            Text(text = nativeAd.headline, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(text = nativeAd.bodyText, color = OmniTextSecondary, fontSize = 10.sp, maxLines = 1)
                        }
                        Button(
                            onClick = { viewModel.onAdClick(nativeAd.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = OmniAmber, contentColor = OmniObsidian),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = nativeAd.callToAction, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Featured Lossless Audio Tracks
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FEATURED HI-RES MUSIC",
                        color = OmniTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "View Library",
                        color = OmniCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.navigateTo(OmniNavigationItem.MUSIC) }
                    )
                }

                val audios = mediaList.filter { it.mediaType == MediaType.AUDIO }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    audios.forEach { audio ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.playTrack(audio) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniBorder)))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    AsyncImage(
                                        model = audio.thumbnailUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column {
                                        Text(text = audio.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text(text = "${audio.artist} • ${audio.format}", color = OmniTextSecondary, fontSize = 10.sp)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { viewModel.toggleFavorite(audio.id) }) {
                                        Icon(
                                            imageVector = if (audio.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (audio.isFavorite) OmniRose else OmniTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(onClick = { viewModel.playTrack(audio) }) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = "Play",
                                            tint = OmniCyan,
                                            modifier = Modifier.size(24.dp)
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
}

@Composable
fun QuickModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, tint.copy(alpha = 0.3f))))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = tint, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = OmniTextSecondary, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun TrendingVideoCard(
    video: MediaItem,
    onClick: () -> Unit,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = OmniSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(OmniBorder, OmniBorder)))
    ) {
        Column {
            Box(modifier = Modifier.height(110.dp).fillMaxWidth()) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp)
                ) {
                    Text(
                        text = video.resolution,
                        color = OmniCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = video.title,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = video.artist,
                    color = OmniTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${video.sizeBytes / 1000000} MB",
                        color = OmniTextMuted,
                        fontSize = 10.sp
                    )
                    IconButton(
                        onClick = onDownload,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download video",
                            tint = OmniCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
