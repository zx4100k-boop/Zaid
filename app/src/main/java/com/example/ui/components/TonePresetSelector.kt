package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.VoiceTone
import com.example.ui.theme.CyanWave
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IndigoLight

@Composable
fun TonePresetSelector(
    selectedTone: VoiceTone,
    onToneSelected: (VoiceTone) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = "أنماط النبرة",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "أنماط ونبرات الصوت الاحترافية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "اختر أسلوب الإلقاء",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VoiceTone.entries.forEach { tone ->
                val isSelected = tone == selectedTone
                ToneChipCard(
                    tone = tone,
                    isSelected = isSelected,
                    onClick = { onToneSelected(tone) }
                )
            }
        }
    }
}

@Composable
private fun ToneChipCard(
    tone: VoiceTone,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (tone) {
        VoiceTone.DOCUMENTARY -> Icons.Default.AutoStories
        VoiceTone.NEWS -> Icons.Default.Campaign
        VoiceTone.EDUCATIONAL -> Icons.Default.School
        VoiceTone.STORYTELLER -> Icons.Default.TheaterComedy
        VoiceTone.ENERGETIC -> Icons.Default.ElectricBolt
        VoiceTone.NATURAL -> Icons.Default.GraphicEq
    }

    val accentColor = when (tone) {
        VoiceTone.DOCUMENTARY -> CyanWave
        VoiceTone.NEWS -> IndigoLight
        VoiceTone.EDUCATIONAL -> GoldAccent
        VoiceTone.STORYTELLER -> Color(0xFFF472B6)
        VoiceTone.ENERGETIC -> Color(0xFFF97316)
        VoiceTone.NATURAL -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .width(148.dp)
            .clickable(onClick = onClick)
            .testTag("tone_chip_${tone.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, accentColor)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    BoxCenter {
                        Icon(
                            imageVector = icon,
                            contentDescription = tone.titleAr,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (isSelected) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = accentColor
                    ) {
                        Text(
                            text = "نشط",
                            color = Color.Black,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tone.titleAr,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = tone.descriptionAr,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun BoxCenter(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(0.dp)
    ) {
        content()
    }
}
