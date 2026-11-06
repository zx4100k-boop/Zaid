package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanWave
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IndigoLight
import kotlin.math.sin

@Composable
fun AnimatedWaveformVisualizer(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformAnimation")
    
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val ambientGradient = listOf(
        primaryColor.copy(alpha = 0.7f),
        primaryColor.copy(alpha = 0.3f)
    )
    val speakingGradient = listOf(GoldAccent, CyanWave, IndigoLight)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("waveform_visualizer"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
            val barCount = 38
            val totalWidth = size.width
            val barSpacing = totalWidth / barCount
            val barWidth = barSpacing * 0.55f
            val centerY = size.height / 2f

            for (i in 0 until barCount) {
                val x = i * barSpacing + (barSpacing - barWidth) / 2f
                
                val normalizedIndex = i.toFloat() / barCount
                // Base sine wave
                val waveHeight = if (isSpeaking) {
                    val sine = sin(normalizedIndex * 9f + phase) * 0.5f + 0.5f
                    val secondHarmonic = sin(normalizedIndex * 15f - phase * 1.5f) * 0.3f
                    val rawAmplitude = (sine + secondHarmonic).coerceIn(0.15f, 1.0f)
                    rawAmplitude * (size.height * 0.42f) * pulseScale
                } else {
                    // Gentle ambient wave
                    val ambient = sin(normalizedIndex * 4f + phase * 0.4f) * 0.2f + 0.25f
                    ambient * (size.height * 0.22f)
                }

                val top = (centerY - waveHeight).coerceAtLeast(4f)
                val barHeight = (waveHeight * 2f).coerceAtLeast(6f)

                val gradient = Brush.verticalGradient(
                    colors = if (isSpeaking) speakingGradient else ambientGradient,
                    startY = top,
                    endY = top + barHeight
                )

                drawRoundRect(
                    brush = gradient,
                    topLeft = Offset(x, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }
    }
}
