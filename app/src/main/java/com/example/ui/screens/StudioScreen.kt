package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ai.MakharijAnalysisResult
import com.example.data.local.CustomVoiceEntity
import com.example.tts.TtsPlaybackState
import com.example.ui.components.AddCustomVoiceBottomSheet
import com.example.ui.components.AnimatedWaveformVisualizer
import com.example.ui.components.CustomVoiceSelectorRow
import com.example.ui.components.PitchRateControlPanel
import com.example.ui.components.SampleTextPickerBottomSheet
import com.example.ui.components.TonePresetSelector
import com.example.ui.theme.CyanWave
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.RoseError
import com.example.ui.viewmodel.MainViewModel

@Composable
fun StudioScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val pitch by viewModel.pitch.collectAsStateWithLifecycle()
    val speechRate by viewModel.speechRate.collectAsStateWithLifecycle()
    val selectedTone by viewModel.selectedTone.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val isDiacritizing by viewModel.isDiacritizing.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val isEnhancingTone by viewModel.isEnhancingTone.collectAsStateWithLifecycle()
    val analysisResult by viewModel.analysisResult.collectAsStateWithLifecycle()

    val customVoices by viewModel.customVoices.collectAsStateWithLifecycle()
    val selectedCustomVoice by viewModel.selectedCustomVoice.collectAsStateWithLifecycle()
    val isPlayingPreview by viewModel.isPlayingPreview.collectAsStateWithLifecycle()
    val playingVoicePath by viewModel.currentlyPlayingPreviewPath.collectAsStateWithLifecycle()

    var showSamplePicker by remember { mutableStateOf(false) }
    var showAddVoiceSheet by remember { mutableStateOf(false) }

    val isSpeaking = playbackState is TtsPlaybackState.Speaking

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("studio_screen")
    ) {
        // Hero Card with generated visual asset
        HeroHeaderCard(onOpenSamples = { showSamplePicker = true })

        Spacer(modifier = Modifier.height(16.dp))

        // Main Text Input Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("text_input_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        text = "النص العربي المراد تحويله إلى صوت",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = clipboard.primaryClip
                                if (clip != null && clip.itemCount > 0) {
                                    val pasted = clip.getItemAt(0).text?.toString() ?: ""
                                    viewModel.setInputText(pasted)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("paste_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "لصق النص",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.setInputText("") },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("clear_text_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "مسح النص",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.setInputText(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .testTag("arabic_text_field"),
                    placeholder = {
                        Text(
                            text = "اكتب أو الصق النص العربي هنا لتشكيله بالذكاء الاصطناعي وتحويله إلى صوت فصيح...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 17.sp,
                        lineHeight = 28.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Counts and Stripping
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val wordCount = if (inputText.isBlank()) 0 else inputText.trim().split(Regex("\\s+")).size
                    Text(
                        text = "${inputText.length} حرفاً • $wordCount كلمة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = { viewModel.removeDiacritics() },
                        modifier = Modifier.testTag("remove_tashkeel_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("إزالة التشكيل", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AI Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Smart Tashkeel Button
                    Button(
                        onClick = { viewModel.requestSmartDiacritization() },
                        enabled = !isDiacritizing && !isAnalyzing,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_diacritize_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isDiacritizing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("جاري التشكيل...", style = MaterialTheme.typography.labelMedium)
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "تشكيل ذكي",
                                modifier = Modifier.size(16.dp),
                                tint = GoldAccent
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تشكيل ذكي بالـ AI", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    // Makharij Audit Button
                    OutlinedButton(
                        onClick = { viewModel.requestMakharijAnalysis() },
                        enabled = !isDiacritizing && !isAnalyzing,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_makharij_audit_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("جاري التحليل...", style = MaterialTheme.typography.labelMedium)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Hearing,
                                contentDescription = "تدقيق المخارج",
                                modifier = Modifier.size(16.dp),
                                tint = CyanWave
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تدقيق المخارج", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Voices Row (Recorded & Uploaded Voices)
        CustomVoiceSelectorRow(
            customVoices = customVoices,
            selectedVoice = selectedCustomVoice,
            isPlayingPreview = isPlayingPreview,
            playingVoicePath = playingVoicePath,
            onSelectVoice = { viewModel.selectCustomVoice(it) },
            onClearSelection = { viewModel.clearCustomVoiceSelection() },
            onAddNewVoice = { showAddVoiceSheet = true },
            onPlayPreview = { viewModel.playPreviewAudio(it) },
            onStopPreview = { viewModel.stopPreviewAudio() }
        )

        // Active Custom Voice Banner if selected
        if (selectedCustomVoice != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GoldAccent.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth().testTag("active_custom_voice_banner")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "✨ الصوت النشط: ${selectedCustomVoice!!.voiceName}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Text(
                            text = "النمط: ${selectedCustomVoice!!.toneLabel} • نبرة ${String.format(java.util.Locale.US, "%.2f", selectedCustomVoice!!.pitch)}x",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            val path = selectedCustomVoice!!.sourcePath
                            if (isPlayingPreview && playingVoicePath == path) {
                                viewModel.stopPreviewAudio()
                            } else {
                                viewModel.playPreviewAudio(path)
                            }
                        },
                        modifier = Modifier.size(36.dp).testTag("preview_active_voice_btn")
                    ) {
                        Icon(
                            imageVector = if (isPlayingPreview && playingVoicePath == selectedCustomVoice!!.sourcePath) {
                                Icons.Default.Stop
                            } else {
                                Icons.Default.PlayArrow
                            },
                            contentDescription = "استماع للأصل",
                            tint = GoldAccent
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tone Presets Selector
        TonePresetSelector(
            selectedTone = selectedTone,
            onToneSelected = { viewModel.selectTone(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pitch & Speech Rate Slider Panel
        PitchRateControlPanel(
            pitch = pitch,
            rate = speechRate,
            onPitchChange = { viewModel.setPitch(it) },
            onRateChange = { viewModel.setSpeechRate(it) },
            onReset = {
                viewModel.setPitch(1.0f)
                viewModel.setSpeechRate(1.0f)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Audio Player & Playback Card
        AudioPlaybackCard(
            playbackState = playbackState,
            isSpeaking = isSpeaking,
            onPlay = { viewModel.speakCurrentText() },
            onStop = { viewModel.stopSpeaking() }
        )

        // Makharij Analysis Result Card (if available)
        AnimatedVisibility(
            visible = analysisResult != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            analysisResult?.let { analysis ->
                Spacer(modifier = Modifier.height(16.dp))
                MakharijAnalysisCard(analysis = analysis)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }

    if (showSamplePicker) {
        SampleTextPickerBottomSheet(
            onDismiss = { showSamplePicker = false },
            onSelectSample = { sample ->
                viewModel.setInputText(sample.text)
                viewModel.selectTone(sample.recommendedTone)
            }
        )
    }

    if (showAddVoiceSheet) {
        AddCustomVoiceBottomSheet(
            viewModel = viewModel,
            onDismiss = { showAddVoiceSheet = false }
        )
    }
}

@Composable
private fun HeroHeaderCard(onOpenSamples: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_header_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.hero_banner),
                contentDescription = "ناطق النصوص بالذكاء الاصطناعي",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Crop
            )

            // Scrim overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

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
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                    ) {
                        Text(
                            text = "صوت احترافي مدعوم بالـ AI",
                            color = GoldAccent,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenSamples,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Black.copy(alpha = 0.4f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("open_samples_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "نماذج",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نماذج جاهزة", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ناطق النصوص ومخارج الحروف",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "تحويل النص إلى صوت فصيح مع تحكم كامل بنبرة الصوت ومخارج الحروف",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun AudioPlaybackCard(
    playbackState: TtsPlaybackState,
    isSpeaking: Boolean,
    onPlay: () -> Unit,
    onStop: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("audio_playback_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المشغل الصوتي والترددات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val (statusText, statusColor) = when (playbackState) {
                    is TtsPlaybackState.Speaking -> "جاري التحدث الآن..." to EmeraldSuccess
                    is TtsPlaybackState.Initializing -> "تهيئة المحرك..." to GoldAccent
                    is TtsPlaybackState.Finished -> "اكتمل النطق بنجاح" to IndigoLight
                    is TtsPlaybackState.Error -> "تنبيه صوتي" to RoseError
                    else -> "جاهز للنطق" to MaterialTheme.colorScheme.primary
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Visualizer
            AnimatedWaveformVisualizer(
                isSpeaking = isSpeaking,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Play / Stop Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (isSpeaking) onStop() else onPlay()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(52.dp)
                        .testTag("main_play_stop_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSpeaking) RoseError else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isSpeaking) "إيقاف الصوت" else "تشغيل الصوت الفصيح",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSpeaking) "إيقاف القراءة" else "نطق النص بصوت احترافي",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MakharijAnalysisCard(analysis: MakharijAnalysisResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("makharij_analysis_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyanWave.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Hearing,
                    contentDescription = "تحليل مخارج الحروف",
                    tint = CyanWave,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تقرير مخارج الحروف الصوتية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CyanWave
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = analysis.summary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (analysis.heavyLetters.isNotEmpty()) {
                Text(
                    text = "حروف الاستعلاء والتفخيم الواردة:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    analysis.heavyLetters.forEach { letter ->
                        Surface(
                            shape = CircleShape,
                            color = GoldAccent.copy(alpha = 0.2f),
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = letter,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (analysis.throatLetters.isNotEmpty()) {
                Text(
                    text = "حروف الحلق (الهمزة، الهاء، العين، الحاء، الغين، الخاء):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = IndigoLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    analysis.throatLetters.forEach { letter ->
                        Surface(
                            shape = CircleShape,
                            color = IndigoLight.copy(alpha = 0.2f),
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = letter,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoLight,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
                text = "نصيحة الإلقاء الصوتي:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = analysis.vocalizationAdvice,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "مواضع الوقف والتنفس: ${analysis.pausePointsTip}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
