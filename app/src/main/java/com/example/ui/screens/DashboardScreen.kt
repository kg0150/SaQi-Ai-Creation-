package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
import com.example.ui.components.VideoPreviewPlayer
import com.example.ui.viewmodel.StudioTab
import com.example.ui.viewmodel.VideoStudioViewModel

@Composable
fun DashboardScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentProject = viewModel.currentProject.value
    val projects = viewModel.projects.value
    val isPlaying = viewModel.isPlaying.value
    val currentTime = viewModel.playbackTime.value
    val selectedSceneIndex = viewModel.selectedSceneIndex.value
    val selectedAspectRatio = viewModel.selectedAspectRatio.value
    val statusBanner = viewModel.statusBanner.value

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Platform Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CINECRAFT AI",
                        color = Color(0xFFF6AD55),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Cloud AI Video Studio & 13-Agent Engine",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x3310B981),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6610B981))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CLOUD WORKER READY",
                            color = Color(0xFF34D399),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Notification Banner
        if (statusBanner != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF162035),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3E66)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = statusBanner,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }

        // Quick Create Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF131A2B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.setTab(StudioTab.NEW_PROJECT) }
                    .testTag("create_new_project_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CREATE NEW AI VIDEO",
                            color = Color(0xFFF6AD55),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enter idea -> 13 Agents generate map, script, voice, 4K render & QA",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { viewModel.setTab(StudioTab.NEW_PROJECT) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6AD55)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("+ Create", color = Color(0xFF090D16), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Active Project Video Preview Player
        if (currentProject != null) {
            item {
                Text(
                    text = "ACTIVE PRODUCTION PROJECT",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                VideoPreviewPlayer(
                    project = currentProject,
                    isPlaying = isPlaying,
                    currentTimeSeconds = currentTime,
                    selectedSceneIndex = selectedSceneIndex,
                    selectedAspectRatio = selectedAspectRatio,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onSeek = { viewModel.seekTo(it) },
                    onSelectAspectRatio = { viewModel.setAspectRatio(it) },
                    onSelectSceneIndex = { viewModel.selectSceneByIndex(it) }
                )
            }

            // Quick Studio Hub Navigation Pills
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HubButton(
                        title = "AI Pipeline",
                        icon = Icons.Default.AutoAwesome,
                        badge = if (currentProject.status == ProjectStatus.NEEDS_FIX) "Fix" else "13 Agents",
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setTab(StudioTab.PIPELINE) }
                    )
                    HubButton(
                        title = "Map Studio",
                        icon = Icons.Default.Explore,
                        badge = "3D Route",
                        color = Color(0xFFF6AD55),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setTab(StudioTab.MAP_STUDIO) }
                    )
                    HubButton(
                        title = "Storyboard",
                        icon = Icons.Default.ViewTimeline,
                        badge = "${currentProject.scenes.size} Scenes",
                        color = Color(0xFF818CF8),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setTab(StudioTab.STORYBOARD) }
                    )
                    HubButton(
                        title = "Export",
                        icon = Icons.Default.Download,
                        badge = "Package",
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setTab(StudioTab.DELIVERY) }
                    )
                }
            }

            // Error Center Alert Pill if project needs fix
            if (currentProject.status == ProjectStatus.NEEDS_FIX) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x33EF4444),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.setTab(StudioTab.ERROR_CENTER) }
                            .testTag("error_center_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "QA Detected 1 Error • Scene 4 Voice Pronunciation",
                                        color = Color(0xFFFCA5A5),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Tap to review or execute 1-Click Auto-Repair Feedback Loop",
                                        color = Color(0xFFF87171),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Button(
                                onClick = { viewModel.triggerAutoRepair() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Auto-Fix", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Recent Projects Section
        item {
            Text(
                text = "MY PROJECTS",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(projects) { proj ->
            val isSelected = proj.id == currentProject?.id
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFF1E283F) else Color(0xFF121726),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) Color(0xFFF6AD55) else Color(0xFF202A3E)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.selectProject(proj.id) }
                    .testTag("project_item_${proj.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = proj.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${proj.durationSeconds.toInt()}s • ${proj.format.badge} • ${proj.scenes.size} Scenes • ${proj.createdAt}",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (proj.status) {
                            ProjectStatus.APPROVED, ProjectStatus.COMPLETED -> Color(0x3310B981)
                            ProjectStatus.NEEDS_FIX -> Color(0x33EF4444)
                            else -> Color(0x33F59E0B)
                        }
                    ) {
                        Text(
                            text = proj.status.label.uppercase(),
                            color = when (proj.status) {
                                ProjectStatus.APPROVED, ProjectStatus.COMPLETED -> Color(0xFF34D399)
                                ProjectStatus.NEEDS_FIX -> Color(0xFFF87171)
                                else -> Color(0xFFFBBF24)
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HubButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF131A2B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(badge, color = color, fontSize = 9.sp, fontWeight = FontWeight.Medium)
        }
    }
}
