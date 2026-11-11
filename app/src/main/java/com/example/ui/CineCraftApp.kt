package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ProjectStatus
import com.example.ui.screens.*
import com.example.ui.viewmodel.StudioTab
import com.example.ui.viewmodel.VideoStudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineCraftApp(
    viewModel: VideoStudioViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentProject by viewModel.currentProject.collectAsState()

    // Back handling: If on any screen other than Dashboard, back returns to Dashboard
    BackHandler(enabled = currentTab != StudioTab.DASHBOARD) {
        viewModel.setTab(StudioTab.DASHBOARD)
    }

    val hasErrors = currentProject?.status == ProjectStatus.NEEDS_FIX

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF6AD55),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = Color(0xFF090D16),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CINECRAFT AI",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = currentProject?.title ?: "AI Video Studio",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                maxLines = 1
                            )
                        }
                    }
                },
                actions = {
                    // Error Center Action Icon
                    IconButton(
                        onClick = { viewModel.setTab(StudioTab.ERROR_CENTER) },
                        modifier = Modifier.testTag("nav_top_error_center")
                    ) {
                        BadgedBox(
                            badge = {
                                if (hasErrors) {
                                    Badge(containerColor = Color(0xFFEF4444)) {
                                        Text("1", color = Color.White, fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (hasErrors) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = "Errors",
                                tint = if (hasErrors) Color(0xFFEF4444) else Color(0xFF10B981)
                            )
                        }
                    }

                    // Thumbnail Studio Action Icon
                    IconButton(
                        onClick = { viewModel.setTab(StudioTab.THUMBNAILS) },
                        modifier = Modifier.testTag("nav_top_thumbnails")
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = "Thumbnails",
                            tint = if (currentTab == StudioTab.THUMBNAILS) Color(0xFFF6AD55) else Color(0xFFCBD5E1)
                        )
                    }

                    // Cloud Settings Action Icon
                    IconButton(
                        onClick = { viewModel.setTab(StudioTab.SETTINGS) },
                        modifier = Modifier.testTag("nav_top_settings")
                    ) {
                        Icon(
                            Icons.Default.Cloud,
                            contentDescription = "Settings",
                            tint = if (currentTab == StudioTab.SETTINGS) Color(0xFF38BDF8) else Color(0xFFCBD5E1)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B0F19),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0B0F19),
                contentColor = Color(0xFF94A3B8),
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .border(1.dp, Color(0xFF1E283F))
                    .testTag("bottom_navigation_bar")
            ) {
                val navItems = listOf(
                    Triple(StudioTab.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
                    Triple(StudioTab.NEW_PROJECT, "New Video", Icons.Default.AddCircleOutline),
                    Triple(StudioTab.PIPELINE, "AI Pipeline", Icons.Default.AutoAwesome),
                    Triple(StudioTab.STORYBOARD, "Storyboard", Icons.Default.ViewTimeline),
                    Triple(StudioTab.MAP_STUDIO, "Map Studio", Icons.Default.Explore),
                    Triple(StudioTab.DELIVERY, "Export", Icons.Default.Download)
                )

                navItems.forEach { (tab, label, icon) ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color(0xFFF6AD55) else Color(0xFF64748B)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFFF6AD55) else Color(0xFF64748B),
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0x33F6AD55)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        containerColor = Color(0xFF090D16)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .widthIn(max = 760.dp) // Responsive optimization for tablets / wide screens
        ) {
            when (currentTab) {
                StudioTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                StudioTab.NEW_PROJECT -> NewProjectScreen(viewModel = viewModel)
                StudioTab.PIPELINE -> PipelineScreen(viewModel = viewModel)
                StudioTab.STORYBOARD -> StoryboardScreen(viewModel = viewModel)
                StudioTab.MAP_STUDIO -> MapStudioScreen(viewModel = viewModel)
                StudioTab.ERROR_CENTER -> ErrorCenterScreen(viewModel = viewModel)
                StudioTab.THUMBNAILS -> ThumbnailStudioScreen(viewModel = viewModel)
                StudioTab.DELIVERY -> FinalDeliveryScreen(viewModel = viewModel)
                StudioTab.SETTINGS -> CloudSettingsScreen(viewModel = viewModel)
            }
        }
    }
}
