package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.VoiceTone

data class SampleTextItem(
    val title: String,
    val category: String,
    val recommendedTone: VoiceTone,
    val text: String
)

val ArabicSampleTexts = listOf(
    SampleTextItem(
        title = "فصاحة لغة الضاد ومخارج الحروف",
        category = "لغوي وأدبي",
        recommendedTone = VoiceTone.EDUCATIONAL,
        text = "إِنَّ اللُّغَةَ العَرَبِيَّةَ تَتَمَيَّزُ بِفَصَاحَةِ أَلْفَاظِهَا وَدِقَّةِ مَخَارِجِ حُرُوفِهَا، حَيْثُ يُعَدُّ حَرْفُ الضَّادِ وَالقَافِ وَالعَيْنِ مِنْ أَعْرَقِ الحُرُوفِ نُطْقاً فِي مَجْرَى الصَّوْتِ."
    ),
    SampleTextItem(
        title = "سورة الإخلاص الشريفة",
        category = "تلاوة قرآنية دقيقة",
        recommendedTone = VoiceTone.EDUCATIONAL,
        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ. قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ."
    ),
    SampleTextItem(
        title = "أسرار الفضاء والكون",
        category = "وثائقي علمي",
        recommendedTone = VoiceTone.DOCUMENTARY,
        text = "فِي أَعْمَاقِ هَذَا الكَوْنِ الفَسِيحِ، تَتَلَأْلَأُ مَلَايِينُ النُّجُومِ وَالمَجَرَّاتِ، حَيْثُ يُخَيِّمُ السُّكُونُ وَيَتَجَلَّى إِبْدَاعُ الخَالِقِ فِي مَدَارَاتِ الفَلَكِ."
    ),
    SampleTextItem(
        title = "موجز الأخبار والتقارير",
        category = "إخباري رسمي",
        recommendedTone = VoiceTone.NEWS,
        text = "أَهْلاً بِكُمْ فِي هَذَا المَوْجَزِ الإِخْبَارِيِّ المُبَاشِرِ، حَيْثُ تُوَاصِلُ التَّطَوُّرَاتُ التِّقْنِيَّةُ إِحْدَاثَ ثَوْرَةٍ كُبْرَى فِي مَجَالَاتِ الذَّكَاءِ الاِصْطِنَاعِيِّ وَتَحْلِيلِ البَيَانَاتِ."
    ),
    SampleTextItem(
        title = "حكاية العصفور الصغير",
        category = "قصة أطفال دافئة",
        recommendedTone = VoiceTone.STORYTELLER,
        text = "كَانَ يَامَا كَانَ فِي قَدِيمِ الزَّمَانِ، عُصْفُورٌ صَغِيرٌ ذُو رِيشٍ ذَهَبِيٍّ بَرَّاقٍ، يُحَلِّقُ كُلَّ صَبَاحٍ بَيْنَ أَغْصَانِ الأَشْجَارِ يُغَرِّدُ بِأَعْذَبِ الأَلْحَانِ."
    ),
    SampleTextItem(
        title = "انطلاقة نحو القمة",
        category = "إعلاني حماسي",
        recommendedTone = VoiceTone.ENERGETIC,
        text = "لَا تَنْتَظِرِ الفُرْصَةَ، بَلِ اصْنَعْهَا بِنَفْسِكَ! انْطَلِقِ الآنَ نَحْوَ أَهْدَافِكَ وَحَقِّقْ طُمُوحَاتِكَ بِكُلِّ شَغَفٍ وَعَزِيمَةٍ لَا تَلِين."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleTextPickerBottomSheet(
    onDismiss: () -> Unit,
    onSelectSample: (SampleTextItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("sample_text_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "نصوص جاهزة",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "نماذج نصوص عربية للتجربة",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_samples_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ArabicSampleTexts) { sample ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectSample(sample)
                                onDismiss()
                            }
                            .testTag("sample_card_${sample.recommendedTone.name.lowercase()}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sample.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = sample.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = sample.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
