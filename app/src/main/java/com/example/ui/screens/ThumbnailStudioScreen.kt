package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.VideoStudioViewModel

@Composable
fun ThumbnailStudioScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentProject = viewModel.currentProject.value
    var selectedRatio by remember { mutableStateOf("16:9") }

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
            Column {
                Text(
                    text = "AI THUMBNAIL STUDIO",
                    color = Color(0xFFF6AD55),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "5 High-CTR concepts evaluated by Agent 12 (Thumbnail QA)",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Format Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("16:9 (YouTube Desktop/TV)", "9:16 (Shorts/TikTok/Reels)").forEach { ratio ->
                    val isSelected = selectedRatio.startsWith(ratio.take(4))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFFF6AD55) else Color(0xFF161E30),
                        contentColor = if (isSelected) Color(0xFF090D16) else Color(0xFF94A3B8),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedRatio = ratio.take(4) }
                    ) {
                        Text(
                            text = ratio,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // Thumbnail QA Agent Checklist Card
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF10192A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223555)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AGENT 12: THUMBNAIL QA AUDIT (PASSED 95/100)", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("✓ High focal contrast ratio > 7:1 for mobile feeds", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Text("✓ Headline typography placed outside YouTube duration timestamp", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Text("✓ Clean facial and terrain geometry with zero artifacting", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
            }
        }

        // 5 Thumbnail Concepts
        items(currentProject.thumbnailConcepts) { concept ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
                modifier = Modifier.fillMaxWidth().testTag("thumbnail_concept_${concept.id}")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF6AD55)
                            ) {
                                Text(
                                    text = "CONCEPT #${concept.id}",
                                    color = Color(0xFF090D16),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = concept.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = concept.aspectRatio,
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Visual Mockup Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF172033))
                            .border(1.dp, Color(0xFF2E3E5F), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFF6AD55), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xCC090D16)
                            ) {
                                Text(
                                    text = concept.headlineText,
                                    color = Color(0xFFFEF08A),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Concept Details
                    Text(
                        text = "VISUAL CONCEPT: ${concept.visualConcept}",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF131A2B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("IMAGE GENERATION PROMPT:", color = Color(0xFF818CF8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(concept.prompt, color = Color(0xFFCBD5E1), fontSize = 10.sp)
                        }
                    }

                    Text(
                        text = "COMPOSITION: ${concept.composition}",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
