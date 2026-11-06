package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanWave
import com.example.ui.theme.GoldAccent
import java.util.Locale

@Composable
fun PitchRateControlPanel(
    pitch: Float,
    rate: Float,
    onPitchChange: (Float) -> Unit,
    onRateChange: (Float) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pitch_rate_panel"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with reset button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "تحكم الصوت",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "التحكم في نبرة وسرعة الصوت",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onReset,
                    modifier = Modifier.testTag("reset_pitch_rate_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "استعادة الافتراضي",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "إعادة ضبط",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Pitch Control Slider
            val pitchLabel = when {
                pitch < 0.75f -> "نبرة عميقة جداً (باس)"
                pitch < 0.95f -> "نبرة وقورة ورخيمة"
                pitch <= 1.05f -> "نبرة طبيعية متوازنة"
                pitch < 1.35f -> "نبرة مشرقة وحيوية"
                else -> "نبرة حادة ومرتفعة"
            }

            SliderControlRow(
                title = "نبرة الصوت (Pitch)",
                badgeText = String.format(Locale.US, "%.2fx", pitch),
                description = pitchLabel,
                icon = Icons.Default.GraphicEq,
                iconTint = GoldAccent,
                value = pitch,
                valueRange = 0.5f..2.0f,
                onValueChange = onPitchChange,
                testTagPrefix = "pitch"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Speech Rate Control Slider
            val rateLabel = when {
                rate < 0.75f -> "قراءة بطيئة جداً للتدقيق"
                rate < 0.95f -> "قراءة متأنية وواضحة"
                rate <= 1.05f -> "سرعة القراءة المعتدلة"
                rate < 1.35f -> "قراءة سريعة ورشيقة"
                else -> "تصفح صوتي سريع"
            }

            SliderControlRow(
                title = "سرعة النطق (Speed Rate)",
                badgeText = String.format(Locale.US, "%.2fx", rate),
                description = rateLabel,
                icon = Icons.Default.Speed,
                iconTint = CyanWave,
                value = rate,
                valueRange = 0.5f..2.0f,
                onValueChange = onRateChange,
                testTagPrefix = "rate"
            )
        }
    }
}

@Composable
private fun SliderControlRow(
    title: String,
    badgeText: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    testTagPrefix: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onValueChange((value - 0.05f).coerceIn(valueRange)) },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("${testTagPrefix}_decrease_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "إنقاص",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .testTag("${testTagPrefix}_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = iconTint,
                    activeTrackColor = iconTint,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            IconButton(
                onClick = { onValueChange((value + 0.05f).coerceIn(valueRange)) },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("${testTagPrefix}_increase_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "زيادة",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
