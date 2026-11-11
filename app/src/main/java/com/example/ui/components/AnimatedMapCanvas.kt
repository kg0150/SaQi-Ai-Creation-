package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MapStyle
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedMapCanvas(
    modifier: Modifier = Modifier,
    originLabel: String = "Pakistan (Islamabad)",
    destLabel: String = "Saudi Arabia (Riyadh)",
    mapStyle: MapStyle = MapStyle.CINEMATIC,
    progressFraction: Float? = null // If null, auto-loops
) {
    // Infinite transition for continuous flight animation and radar pulsing
    val infiniteTransition = rememberInfiniteTransition(label = "mapFlight")
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flightProgress"
    )

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarPulse"
    )

    val effectiveProgress = progressFraction ?: animatedProgress

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF141C2E),
                        Color(0xFF090D16)
                    )
                )
            )
            .testTag("animated_map_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw subtle latitude / longitude cartographic grid lines
            val gridColor = Color(0x1F64748B)
            val numGridX = 7
            val numGridY = 5
            for (i in 1..numGridX) {
                val x = width * (i.toFloat() / (numGridX + 1))
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            }
            for (j in 1..numGridY) {
                val y = height * (j.toFloat() / (numGridY + 1))
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            }

            // 2. Define Cartographic Anchors (Pakistan on east/top-right, Saudi Arabia on west/lower-left)
            val pOrigin = Offset(width * 0.82f, height * 0.28f)
            val pDest = Offset(width * 0.22f, height * 0.72f)
            // Control point for quadratic curve creating the great-circle arc across the Arabian Sea
            val pControl = Offset(width * 0.45f, height * 0.18f)

            // 3. Draw Route Path Background Glow
            val fullPath = Path().apply {
                moveTo(pOrigin.x, pOrigin.y)
                quadraticTo(pControl.x, pControl.y, pDest.x, pDest.y)
            }

            drawPath(
                path = fullPath,
                color = Color(0x33F6AD55),
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )
            drawPath(
                path = fullPath,
                color = Color(0x66F6AD55),
                style = Stroke(
                    width = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            )

            // 4. Calculate current aircraft position along the quadratic bezier curve
            val t = effectiveProgress.coerceIn(0f, 1f)
            val oneMinusT = 1f - t
            val currentX = oneMinusT * oneMinusT * pOrigin.x + 2 * oneMinusT * t * pControl.x + t * t * pDest.x
            val currentY = oneMinusT * oneMinusT * pOrigin.y + 2 * oneMinusT * t * pControl.y + t * t * pDest.y
            val planePos = Offset(currentX, currentY)

            // 5. Draw active completed trail path up to current t
            val activeTrailPath = Path().apply {
                moveTo(pOrigin.x, pOrigin.y)
                val steps = 40
                val activeSteps = (steps * t).toInt()
                for (s in 1..activeSteps) {
                    val stepT = s.toFloat() / steps
                    val omt = 1f - stepT
                    val px = omt * omt * pOrigin.x + 2 * omt * stepT * pControl.x + stepT * stepT * pDest.x
                    val py = omt * omt * pOrigin.y + 2 * omt * stepT * pControl.y + stepT * stepT * pDest.y
                    lineTo(px, py)
                }
                lineTo(currentX, currentY)
            }

            drawPath(
                path = activeTrailPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B), Color(0xFF38BDF8)),
                    start = pOrigin,
                    end = planePos
                ),
                style = Stroke(width = 4.5f, cap = StrokeCap.Round)
            )

            // 6. Draw Origin Radar Beacon
            drawCircle(
                color = Color(0x3338BDF8),
                radius = pulseRadius,
                center = pOrigin
            )
            drawCircle(
                color = Color(0xFF38BDF8),
                radius = 6f,
                center = pOrigin
            )
            drawCircle(
                color = Color.White,
                radius = 2.5f,
                center = pOrigin
            )

            // 7. Draw Destination Radar Beacon
            drawCircle(
                color = Color(0x33F6AD55),
                radius = pulseRadius,
                center = pDest
            )
            drawCircle(
                color = Color(0xFFF6AD55),
                radius = 7f,
                center = pDest
            )
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = pDest
            )

            // 8. Draw Aircraft / Moving Node
            // Compute tangent angle for orientation
            val tangentX = 2 * (1 - t) * (pControl.x - pOrigin.x) + 2 * t * (pDest.x - pControl.x)
            val tangentY = 2 * (1 - t) * (pControl.y - pOrigin.y) + 2 * t * (pDest.y - pControl.y)

            drawCircle(
                color = Color(0x66FFFFFF),
                radius = 16f,
                center = planePos
            )
            drawCircle(
                color = Color(0xFFFBBF24),
                radius = 8f,
                center = planePos
            )
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = planePos
            )

            // Heading Vector Indicator
            val dirLen = 22f
            val norm = kotlin.math.sqrt(tangentX * tangentX + tangentY * tangentY).coerceAtLeast(1f)
            val dirX = (tangentX / norm) * dirLen
            val dirY = (tangentY / norm) * dirLen
            drawLine(
                color = Color(0xFFFBBF24),
                start = planePos,
                end = Offset(planePos.x + dirX, planePos.y + dirY),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        }

        // Overlays: Badges and HUD Info
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color(0xCC0B0F19), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF10B981), RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "STYLE: ${mapStyle.label.uppercase()}",
                color = Color(0xFFF8FAFC),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Origin Label
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 22.dp, end = 14.dp)
                .background(Color(0xD90B0F19), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "ORIGIN",
                color = Color(0xFF38BDF8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = originLabel,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Destination Label
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 22.dp, start = 14.dp)
                .background(Color(0xD90B0F19), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "DESTINATION",
                color = Color(0xFFF6AD55),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = destLabel,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Telemetry HUD bottom right
        Text(
            text = "ALT: 38,000 FT • SPEED: MACH 0.84",
            color = Color(0x9994A3B8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        )
    }
}
