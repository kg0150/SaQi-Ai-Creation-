package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.QualityScores

@Composable
fun QualityScoreCard(
    scores: QualityScores,
    modifier: Modifier = Modifier
) {
    val isApproved = scores.isApproved()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0F1422))
            .border(
                1.dp,
                if (isApproved) Color(0xFF10B981).copy(alpha = 0.5f) else Color(0xFFF59E0B).copy(alpha = 0.5f),
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
            .testTag("quality_score_card")
    ) {
        // Overall Score Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI QUALITY AUDIT SCORE",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${scores.overallQuality}",
                        color = if (isApproved) Color(0xFF34D399) else Color(0xFFFBBF24),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = " / 100",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isApproved) Color(0x3310B981) else Color(0x33EF4444),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isApproved) Color(0xFF10B981) else Color(0xFFEF4444)
                )
            ) {
                Text(
                    text = if (isApproved) "✓ APPROVED (>90)" else "⚠️ NEEDS AUTO-FIX (<90)",
                    color = if (isApproved) Color(0xFF34D399) else Color(0xFFF87171),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Bar for Overall Score
        LinearProgressIndicator(
            progress = { scores.overallQuality / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (isApproved) Color(0xFF10B981) else Color(0xFFF59E0B),
            trackColor = Color(0xFF1E283F),
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Grid of 8 Sub-Scores
        val subScores = listOf(
            "Script Quality" to scores.scriptQuality,
            "Voice Quality" to scores.voiceQuality,
            "Visual Quality" to scores.visualQuality,
            "Map Accuracy" to scores.mapAccuracy,
            "Subtitle Quality" to scores.subtitleQuality,
            "Audio Quality" to scores.audioQuality,
            "Editing Quality" to scores.editingQuality,
            "Thumbnail Quality" to scores.thumbnailQuality
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            for (i in subScores.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val first = subScores[i]
                    val second = subScores.getOrNull(i + 1)

                    ScorePill(
                        title = first.first,
                        score = first.second,
                        modifier = Modifier.weight(1f)
                    )

                    if (second != null) {
                        ScorePill(
                            title = second.first,
                            score = second.second,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ScorePill(
    title: String,
    score: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF141B2D),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222E46)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                maxLines = 1
            )
            Text(
                text = "$score",
                color = if (score >= 90) Color(0xFF34D399) else Color(0xFFF87171),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
