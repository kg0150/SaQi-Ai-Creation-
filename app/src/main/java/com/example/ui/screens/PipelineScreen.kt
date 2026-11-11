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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectStatus
import com.example.ui.components.AgentPipelineVisualizer
import com.example.ui.components.QualityScoreCard
import com.example.ui.viewmodel.VideoStudioViewModel

@Composable
fun PipelineScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentProject = viewModel.currentProject.value
    val isPipelineRunning = viewModel.isPipelineRunning.value
    val pipelineLogs = viewModel.pipelineLogs.value
    val inspectedAgent = viewModel.inspectedAgent.value

    if (currentProject == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active project. Please create or select a project.", color = Color(0xFF94A3B8))
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
        // Cockpit Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "13-AGENT PRODUCTION PIPELINE",
                        color = Color(0xFFF6AD55),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = currentProject.title,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (currentProject.status) {
                        ProjectStatus.APPROVED -> Color(0x3310B981)
                        ProjectStatus.NEEDS_FIX -> Color(0x33EF4444)
                        else -> Color(0x33F59E0B)
                    }
                ) {
                    Text(
                        text = currentProject.status.label.uppercase(),
                        color = when (currentProject.status) {
                            ProjectStatus.APPROVED -> Color(0xFF34D399)
                            ProjectStatus.NEEDS_FIX -> Color(0xFFF87171)
                            else -> Color(0xFFFBBF24)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Automatic Error Correction Action Bar (When status is NEEDS_FIX)
        if (currentProject.status == ProjectStatus.NEEDS_FIX || currentProject.qualityScores.overallQuality < 90) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF201318),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth().testTag("auto_fix_callout")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AUTOMATIC ERROR-CORRECTION REQUIRED",
                                color = Color(0xFFFCA5A5),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Text(
                            text = "Agent 5 (Voice QA) detected pronunciation irregularity in Scene 4 ('${currentProject.mapConfig.destinationCity}'). Overall score is ${currentProject.qualityScores.overallQuality}/100, which is below the mandatory 90 approval threshold.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )

                        Button(
                            onClick = { viewModel.triggerAutoRepair() },
                            enabled = !isPipelineRunning,
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("trigger_auto_repair_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPipelineRunning) "AUTO-FIXING IN PROGRESS..." else "EXECUTE AUTO-REPAIR LOOP (FIX & ELEVATE TO 97/100)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Quality Scores Card
        item {
            QualityScoreCard(scores = currentProject.qualityScores)
        }

        // Agent Pipeline Visualizer (All 13 Agents)
        item {
            AgentPipelineVisualizer(
                agents = currentProject.agentEvaluations,
                inspectedAgent = inspectedAgent,
                onSelectAgent = { viewModel.inspectAgent(it) }
            )
        }

        // Real-Time Agent Logs Console
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF070A10),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F293D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI PRODUCTION PIPELINE LOGS",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        if (isPipelineRunning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color(0xFFF6AD55),
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (pipelineLogs.isEmpty()) {
                        Text(
                            text = "> Pipeline idle. Tap 'Run 13-Agent Audit' to start fresh evaluation.",
                            color = Color(0xFF475569),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        pipelineLogs.takeLast(8).forEach { logLine ->
                            Text(
                                text = "> $logLine",
                                color = when {
                                    logLine.contains("ALERT") || logLine.contains("flagged") -> Color(0xFFF87171)
                                    logLine.contains("PASSED") || logLine.contains("APPROVAL") -> Color(0xFF34D399)
                                    logLine.contains("AUTO-REPAIR") -> Color(0xFF38BDF8)
                                    else -> Color(0xFF94A3B8)
                                },
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        // Full Re-run Pipeline Button
        item {
            OutlinedButton(
                onClick = { viewModel.runFullPipeline() },
                enabled = !isPipelineRunning,
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("rerun_pipeline_button"),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF38BDF8))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RE-RUN FULL 13-AGENT AUDIT",
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
