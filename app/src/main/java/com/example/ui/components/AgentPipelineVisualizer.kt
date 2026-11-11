package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentEvaluationItem
import com.example.data.model.AgentStatus

@Composable
fun AgentPipelineVisualizer(
    agents: List<AgentEvaluationItem>,
    inspectedAgent: AgentEvaluationItem?,
    onSelectAgent: (AgentEvaluationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0F1422))
            .border(1.dp, Color(0xFF26334D), RoundedCornerShape(18.dp))
            .padding(14.dp)
            .testTag("agent_pipeline_visualizer")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "13-AGENT AI QUALITY PIPELINE",
                    color = Color(0xFFF6AD55),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Specialized multi-agent verification & auto-repair loop",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            val approvedCount = agents.count { it.status == AgentStatus.APPROVED || it.status == AgentStatus.PASSED }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (approvedCount == 13) Color(0x3310B981) else Color(0x33F59E0B)
            ) {
                Text(
                    text = "$approvedCount/13 PASS",
                    color = if (approvedCount == 13) Color(0xFF34D399) else Color(0xFFFBBF24),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal scrolling or wrapped list of agent status badges
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            agents.forEach { agent ->
                val isSelected = inspectedAgent?.agentId == agent.agentId
                val statusColor = when (agent.status) {
                    AgentStatus.APPROVED, AgentStatus.PASSED -> Color(0xFF10B981)
                    AgentStatus.FLAGGED -> Color(0xFFEF4444)
                    AgentStatus.AUTO_FIXING -> Color(0xFFF59E0B)
                    AgentStatus.RUNNING -> Color(0xFF38BDF8)
                    AgentStatus.PENDING -> Color(0xFF64748B)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFF1E283F) else Color(0xFF141B2D),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFFF6AD55) else Color(0xFF222E46)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectAgent(agent) }
                        .testTag("agent_item_${agent.agentId}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(statusColor.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${agent.agentId}",
                                    color = statusColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = agent.agentName,
                                    color = Color(0xFFF8FAFC),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = agent.description,
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${agent.score}/100",
                                color = if (agent.score >= 90) Color(0xFF34D399) else Color(0xFFF87171),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = statusColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = agent.status.label.uppercase(),
                                    color = statusColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Inspected Agent Drawer / Bottom Box
        AnimatedVisibility(visible = inspectedAgent != null) {
            inspectedAgent?.let { agent ->
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF172033))
                        .border(1.dp, Color(0xFF334466), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AGENT ${agent.agentId}: ${agent.agentName.uppercase()}",
                            color = Color(0xFFF6AD55),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Score: ${agent.score}/100",
                            color = if (agent.score >= 90) Color(0xFF34D399) else Color(0xFFF87171),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = agent.feedback,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp
                    )

                    if (agent.detectedErrors.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "DETECTED ERRORS:",
                            color = Color(0xFFEF4444),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        agent.detectedErrors.forEach { err ->
                            Text(
                                text = "• $err",
                                color = Color(0xFFFCA5A5),
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (agent.correctionInstructions != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "AUTO-REPAIR DIRECTIVE:",
                            color = Color(0xFF38BDF8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = agent.correctionInstructions ?: "",
                            color = Color(0xFFBAE6FD),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
