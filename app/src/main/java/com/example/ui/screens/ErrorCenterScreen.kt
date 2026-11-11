package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.AgentStatus
import com.example.data.model.ProjectStatus
import com.example.ui.viewmodel.VideoStudioViewModel

@Composable
fun ErrorCenterScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentProject = viewModel.currentProject.value
    val isPipelineRunning = viewModel.isPipelineRunning.value

    if (currentProject == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active project.", color = Color(0xFF94A3B8))
        }
        return
    }

    val flaggedAgents = currentProject.agentEvaluations.filter {
        it.status == AgentStatus.FLAGGED || it.detectedErrors.isNotEmpty()
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
            Column {
                Text(
                    text = "AI ERROR CENTER & REPAIR LOOP",
                    color = Color(0xFFEF4444),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Automated defect detection & surgical regeneration engine",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Project Status Banner
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF131A2B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PROJECT:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(currentProject.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (flaggedAgents.isEmpty()) Color(0x3310B981) else Color(0x33EF4444)
                    ) {
                        Text(
                            text = if (flaggedAgents.isEmpty()) "ALL AUDITS PASSED" else "${flaggedAgents.size} ISSUES FLAGGED",
                            color = if (flaggedAgents.isEmpty()) Color(0xFF34D399) else Color(0xFFF87171),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 1-Click Fix Automatically Primary Button
        if (flaggedAgents.isNotEmpty() || currentProject.qualityScores.overallQuality < 90) {
            item {
                Button(
                    onClick = { viewModel.triggerAutoRepair() },
                    enabled = !isPipelineRunning,
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("fix_automatically_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPipelineRunning) "AUTO-FIXING IN PROGRESS..." else "FIX AUTOMATICALLY (FEEDBACK LOOP)",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        if (flaggedAgents.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F1A28),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Zero Production Errors Found", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "All 13 QA Agents verified the script, audio sync, cartography, typography, and visual artifacts. Overall quality score is ${currentProject.qualityScores.overallQuality}/100.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // List of Flagged Issues
        items(flaggedAgents.size) { idx ->
            val agent = flaggedAgents[idx]
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF16121D),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth().testTag("error_card_${agent.agentId}")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AGENT ${agent.agentId}: ${agent.agentName.uppercase()}",
                            color = Color(0xFFF87171),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0x33EF4444)
                        ) {
                            Text(
                                text = "Score: ${agent.score}/100",
                                color = Color(0xFFFCA5A5),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (agent.affectedSceneId != null) {
                        Text(
                            text = "AFFECTED SCENE: Scene #${agent.affectedSceneId}",
                            color = Color(0xFFFBBF24),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = agent.feedback,
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )

                    if (agent.detectedErrors.isNotEmpty()) {
                        Column {
                            Text("DETECTED PROBLEM:", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            agent.detectedErrors.forEach { err ->
                                Text("• $err", color = Color(0xFFFCA5A5), fontSize = 11.sp)
                            }
                        }
                    }

                    if (agent.correctionInstructions != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF131D2E),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("SURGICAL RE-GENERATION INSTRUCTION:", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(agent.correctionInstructions ?: "", color = Color(0xFFBAE6FD), fontSize = 11.sp)
                            }
                        }
                    }

                    // Granular Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerAutoRepair() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Fix Automatically", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                agent.affectedSceneId?.let { viewModel.regenerateScene(it) }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Regen Scene", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                        }
                    }
                }
            }
        }
    }
}
