package com.example.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniNavigationItem
import com.example.ui.viewmodel.OmniViewModel

@Composable
fun OmniPlayApp(
    viewModel: OmniViewModel = viewModel()
) {
    val currentNav by viewModel.currentNav.collectAsState()
    val isMusicPlaying by viewModel.isMusicPlaying.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize().background(OmniObsidian),
        containerColor = OmniObsidian,
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().background(OmniObsidian)) {
                // Mini Now Playing Bar (if audio is active and user is outside Music tab)
                if (isMusicPlaying && currentNav != OmniNavigationItem.MUSIC) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.navigateTo(OmniNavigationItem.MUSIC) }
                            .testTag("mini_player_bar"),
                        color = OmniSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, OmniCyan.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(28.dp).clip(CircleShape).background(OmniCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(16.dp))
                                }
                                Column {
                                    Text(text = currentTrack.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(text = currentTrack.artist, color = OmniTextSecondary, fontSize = 9.sp, maxLines = 1)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { viewModel.toggleMusicPlayPause() }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Pause, contentDescription = "Pause", tint = OmniCyan, modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = { viewModel.nextTrack() }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                // Primary Bottom Navigation Bar
                NavigationBar(
                    containerColor = OmniSurfaceDark,
                    contentColor = OmniCyan,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("omni_bottom_navigation")
                ) {
                    val navItems = listOf(
                        Triple(OmniNavigationItem.HOME, Icons.Default.Home, "Home"),
                        Triple(OmniNavigationItem.DOWNLOADS, Icons.Default.Download, "Download"),
                        Triple(OmniNavigationItem.MUSIC, Icons.Default.MusicNote, "Music"),
                        Triple(OmniNavigationItem.VIDEO, Icons.Default.SmartDisplay, "Video"),
                        Triple(OmniNavigationItem.STUDIO, Icons.Default.MovieFilter, "Studio"),
                        Triple(OmniNavigationItem.AI_ASSISTANT, Icons.Default.AutoAwesome, "AI Hub"),
                        Triple(OmniNavigationItem.FILES, Icons.Default.Folder, "Files")
                    )

                    navItems.forEach { (item, icon, label) ->
                        val isSelected = currentNav == item
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) OmniCyan else OmniTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    color = if (isSelected) OmniCyan else OmniTextMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = OmniCyan.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentNav) {
                OmniNavigationItem.HOME -> HomeScreen(viewModel)
                OmniNavigationItem.DOWNLOADS -> DownloaderScreen(viewModel)
                OmniNavigationItem.MUSIC -> MusicPlayerScreen(viewModel)
                OmniNavigationItem.VIDEO -> VideoPlayerScreen(viewModel)
                OmniNavigationItem.STUDIO -> VideoStudioScreen(viewModel)
                OmniNavigationItem.AI_ASSISTANT -> AiAssistantScreen(viewModel)
                OmniNavigationItem.FILES -> FileManagerScreen(viewModel)
            }
        }
    }
}
