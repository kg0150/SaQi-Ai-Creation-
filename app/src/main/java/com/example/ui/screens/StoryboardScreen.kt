package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.data.model.SceneItem
import com.example.ui.viewmodel.VideoStudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryboardScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentProject = viewModel.currentProject.value
    var editingScene by remember { mutableStateOf<SceneItem?>(null) }
    var editedNarration by remember { mutableStateOf("") }

    if (currentProject == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active project.", color = Color(0xFF94A3B8))
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI STORYBOARD & TIMELINE",
                        color = Color(0xFFF6AD55),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${currentProject.scenes.size} Scenes • Total: ${currentProject.durationSeconds.toInt()}s",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E283F)
                ) {
                    Text(
                        text = currentProject.format.badge,
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        itemsIndexed(currentProject.scenes) { index, scene ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (scene.isMapScene) Color(0xFFF6AD55).copy(alpha = 0.5f) else Color(0xFF26334D)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("storyboard_scene_${scene.id}")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Scene Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (scene.isMapScene) Color(0xFFF6AD55) else Color(0xFF38BDF8)
                            ) {
                                Text(
                                    text = "SCENE ${index + 1}",
                                    color = Color(0xFF090D16),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = scene.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${scene.durationSeconds}s",
                            color = Color(0xFFF6AD55),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Narration Card
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF161E30),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "NARRATION & VOICE-OVER:",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = scene.narration,
                                color = Color(0xFFF8FAFC),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Visual prompt description
                    Column {
                        Text(
                            text = "AI PROMPT INSTRUCTIONS:",
                            color = Color(0xFF818CF8),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = scene.imagePrompt,
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                    }

                    // Metadata Badges (Camera, Transition, SFX)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SceneBadge(icon = Icons.Default.Videocam, text = scene.cameraMovement, color = Color(0xFF38BDF8))
                        SceneBadge(icon = Icons.Default.SwapHoriz, text = scene.transition, color = Color(0xFF818CF8))
                        SceneBadge(icon = Icons.Default.VolumeUp, text = scene.soundEffect, color = Color(0xFF34D399))
                    }

                    // Scene Status
                    if (scene.status.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (scene.status.contains("Flagged")) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (scene.status.contains("Flagged")) Color(0xFFEF4444) else Color(0xFF10B981),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = scene.status,
                                color = if (scene.status.contains("Flagged")) Color(0xFFFCA5A5) else Color(0xFF34D399),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Divider(color = Color(0xFF1E283F), thickness = 1.dp)

                    // Action Buttons: EDIT, REGENERATE, DUPLICATE, MOVE, DELETE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Edit Button
                            OutlinedButton(
                                onClick = {
                                    editingScene = scene
                                    editedNarration = scene.narration
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("edit_scene_${scene.id}")
                            ) {
                                Text("Edit", fontSize = 11.sp)
                            }

                            // Regenerate Single Scene
                            OutlinedButton(
                                onClick = { viewModel.regenerateScene(scene.id) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("regen_scene_${scene.id}")
                            ) {
                                Text("Regen", fontSize = 11.sp)
                            }

                            // Duplicate Button
                            IconButton(
                                onClick = { viewModel.duplicateScene(scene.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Move Up
                            if (index > 0) {
                                IconButton(
                                    onClick = { viewModel.moveScene(index, index - 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                }
                            }

                            // Move Down
                            if (index < currentProject.scenes.size - 1) {
                                IconButton(
                                    onClick = { viewModel.moveScene(index, index + 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                }
                            }

                            // Delete
                            if (currentProject.scenes.size > 1) {
                                IconButton(
                                    onClick = { viewModel.deleteScene(scene.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Narration Dialog
    if (editingScene != null) {
        AlertDialog(
            onDismissRequest = { editingScene = null },
            title = { Text("Edit Scene Narration") },
            text = {
                OutlinedTextField(
                    value = editedNarration,
                    onValueChange = { editedNarration = it },
                    label = { Text("Narration / Subtitle Text") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(onClick = {
                    editingScene?.let { sc ->
                        viewModel.updateNarration(sc.id, editedNarration)
                    }
                    editingScene = null
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingScene = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SceneBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}
