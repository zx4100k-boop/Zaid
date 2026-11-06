package com.example.ui.screens

import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SettingsVoice
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddCustomVoiceBottomSheet
import com.example.ui.theme.CyanWave
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.RoseError
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val voices by viewModel.availableVoices.collectAsStateWithLifecycle()
    val selectedVoice by viewModel.selectedVoiceName.collectAsStateWithLifecycle()
    val isTtsReady by viewModel.ttsEngine.isInitialized.collectAsStateWithLifecycle()

    val customVoices by viewModel.customVoices.collectAsStateWithLifecycle()
    val isPlayingPreview by viewModel.isPlayingPreview.collectAsStateWithLifecycle()
    val playingVoicePath by viewModel.currentlyPlayingPreviewPath.collectAsStateWithLifecycle()

    var showAddVoiceSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = "إعدادات الصوت ومحرك النطق",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "تخصيص محرك الصوت واختيار نبرات الأصوات العربية المتوفرة",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Engine Status Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("engine_status_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isTtsReady) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isTtsReady) EmeraldSuccess else GoldAccent,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isTtsReady) "محرك الصوت العربي: متصل وجاهز" else "جاري تهيئة محرك الصوت...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isTtsReady) EmeraldSuccess else GoldAccent
                    )
                    Text(
                        text = "يدعم معايير الفصاحة والتردد الصوتي بدقة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Voices Management Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("custom_voices_manager_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "أصواتي المخصصة (${customVoices.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddVoiceSheet = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_custom_voice_settings_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة صوت", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (customVoices.isEmpty()) {
                    Text(
                        text = "لم تقم بإضافة أي صوت مخصص بعد. يمكنك تسجيل صوتك مباشرة أو رفع ملف صوتي (MP3, WAV, M4A) ليقوم الذكاء الاصطناعي بمطابقة نبرته.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        customVoices.forEach { voice ->
                            val isPlayingThis = isPlayingPreview && playingVoicePath == voice.sourcePath
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = voice.voiceName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${voice.toneLabel} • مدة ${voice.durationSeconds}ث • نبرة ${String.format(java.util.Locale.US, "%.2f", voice.pitch)}x",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                if (isPlayingThis) viewModel.stopPreviewAudio() else viewModel.playPreviewAudio(voice.sourcePath)
                                            },
                                            modifier = Modifier.size(32.dp).testTag("play_custom_voice_${voice.id}")
                                        ) {
                                            Icon(
                                                imageVector = if (isPlayingThis) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = "استماع للأصل",
                                                tint = if (isPlayingThis) EmeraldSuccess else CyanWave,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteCustomVoice(voice) },
                                            modifier = Modifier.size(32.dp).testTag("delete_custom_voice_${voice.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "حذف الصوت",
                                                tint = RoseError.copy(alpha = 0.8f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Available Arabic Voices List
        Card(
            modifier = Modifier.fillMaxWidth().testTag("voices_list_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SettingsVoice,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الأصوات العربية المتوفرة في النظام",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${voices.size} أصوات",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (voices.isEmpty()) {
                    Text(
                        text = "يستخدم التطبيق المحرك الافتراضي للغة العربية في الجهاز. يمكنك تثبيت حزم أصوات عربية إضافية عالية الجودة من إعدادات النظام.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                } else {
                    voices.forEach { voiceItem ->
                        val isSelected = voiceItem.name == selectedVoice
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectVoice(voiceItem.name) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.selectVoice(voiceItem.name) },
                                modifier = Modifier.testTag("radio_voice_${voiceItem.name}")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = voiceItem.name.replace("ar-", "صوت عربي: "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "اللغة: ${voiceItem.locale.displayName} • جودة ممتازة",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        try {
                            val intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val intent = Intent("com.android.settings.TTS_SETTINGS")
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("open_tts_settings_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إدارة أصوات النظام وتحميل حزم جديدة")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI & Makharij Features Overview Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("ai_features_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تقنيات الذكاء الاصطناعي في ناطق AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                FeaturePointRow(
                    title = "التشكيل الذكي الفوري (Tashkeel AI)",
                    desc = "ضبط الحركات والشدات والتنوين وهمزات الوصل والقطع إعرابياً بنسبة 100% لمنع أخطاء النطق."
                )

                Spacer(modifier = Modifier.height(8.dp))

                FeaturePointRow(
                    title = "تدقيق مخارج الحروف والتجويد",
                    desc = "تحليل مواضع خروج الحروف (الجوف، الحلق، اللسان، الشفتين، الخيشوم) وتحديد مواضع التفخيم والترقيق."
                )

                Spacer(modifier = Modifier.height(8.dp))

                FeaturePointRow(
                    title = "أنماط النبرة الصوتية الاحترافية",
                    desc = "محاكاة طبقات الصوت للأفلام الوثائقية، النشرات الإخبارية، القصص الدافئة، والإعلانات الحماسية."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About Nateq AI
        Card(
            modifier = Modifier.fillMaxWidth().testTag("about_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ناطق AI - تحويل النصوص إلى صوت احترافي",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "الإصدار 1.0 • مدعوم بمحرك Gemini 3.8 Flash و Android TTS",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تم التطوير خصيصاً لخدمة فصاحة لغة الضاد ومخارج حروفها العريقة.",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldAccent,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }

    if (showAddVoiceSheet) {
        AddCustomVoiceBottomSheet(
            viewModel = viewModel,
            onDismiss = { showAddVoiceSheet = false }
        )
    }
}

@Composable
private fun FeaturePointRow(title: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
