package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.ui.theme.OmniCyan
import com.example.ui.theme.OmniViolet

/**
 * Animated Real-Time Spectrum Waveform Visualizer
 */
@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 28
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = (width / (barCount * 1.5f)).coerceAtLeast(3f)
        val spacing = barWidth * 0.5f

        val gradient = Brush.verticalGradient(
            colors = listOf(OmniCyan, OmniViolet),
            startY = 0f,
            endY = height
        )

        for (i in 0 until barCount) {
            val x = i * (barWidth + spacing)
            val normalizedIdx = i.toFloat() / barCount.toFloat()
            val sinVal = kotlin.math.sin((normalizedIdx * 4.0 * Math.PI + phase).toDouble()).toFloat()
            val cosVal = kotlin.math.cos((normalizedIdx * 2.0 * Math.PI - phase * 0.5).toDouble()).toFloat()

            val barHeight = if (isPlaying) {
                val wave = ((sinVal + cosVal + 2f) / 4f).coerceIn(0.15f, 1f)
                (height * wave).coerceAtLeast(6f)
            } else {
                (height * 0.15f)
            }

            val y = height - barHeight

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}
