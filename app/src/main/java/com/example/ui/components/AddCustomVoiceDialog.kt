package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CyanWave
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseError
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomVoiceBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedTab by remember { mutableIntStateOf(0) }
    var voiceName by remember { mutableStateOf("") }
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    var recordedTempFileExists by remember { mutableStateOf(false) }

    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordingDuration by viewModel.recordingDurationSeconds.collectAsStateWithLifecycle()
    val isAnalyzingVoice by viewModel.isAnalyzingVoice.collectAsStateWithLifecycle()
    val isPlayingPreview by viewModel.isPlayingPreview.collectAsStateWithLifecycle()

    // File picker launcher
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedAudioUri = uri
            if (voiceName.isBlank()) {
                voiceName = "صوت مستورد جديد"
            }
        }
    }

    // Permission launcher for microphone
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }
    val recordPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDeniedMessage = null
            viewModel.startRecording()
        } else {
            permissionDeniedMessage = "مطلوب إذن الميكروفون لتسجيل الصوت المخصص."
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            if (isRecording) viewModel.cancelRecording()
            viewModel.stopPreviewAudio()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("add_custom_voice_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إضافة صوت مخصص جديد",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        if (isRecording) viewModel.cancelRecording()
                        viewModel.stopPreviewAudio()
                        onDismiss()
                    },
                    modifier = Modifier.testTag("close_add_voice_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "اختر الطريقة التي تفضلها لإضافة البصمة الصوتية ومطابقتها بالذكاء الاصطناعي",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Method Tabs: Recording vs File Upload
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        viewModel.stopPreviewAudio()
                        selectedTab = 0
                    },
                    icon = { Icon(imageVector = Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("تسجيل صوتي مباشر", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_record_voice")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        viewModel.stopPreviewAudio()
                        selectedTab = 1
                    },
                    icon = { Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("رفع ملف صوتي", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_upload_voice")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Voice Name Input
            OutlinedTextField(
                value = voiceName,
                onValueChange = { voiceName = it },
                label = { Text("اسم الصوت (مثال: صوتي، صوت الوالد، صوت إذاعي)") },
                modifier = Modifier.fillMaxWidth().testTag("custom_voice_name_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // --- Tab 1: Live Voice Recording ---
                RecordingTabContent(
                    isRecording = isRecording,
                    recordingDuration = recordingDuration,
                    recordedTempFileExists = recordedTempFileExists,
                    isPlayingPreview = isPlayingPreview,
                    permissionError = permissionDeniedMessage,
                    onStartRecord = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            permissionDeniedMessage = null
                            viewModel.startRecording()
                        } else {
                            recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    onStopRecord = {
                        recordedTempFileExists = true
                    },
                    onSave = {
                        viewModel.stopRecordingAndSave(voiceName) { success ->
                            if (success) onDismiss()
                        }
                    },
                    isAnalyzing = isAnalyzingVoice
                )
            } else {
                // --- Tab 2: Upload Audio File ---
                UploadTabContent(
                    selectedUri = selectedAudioUri,
                    onPickFile = { audioPickerLauncher.launch("audio/*") },
                    onSave = {
                        selectedAudioUri?.let { uri ->
                            viewModel.importAudioFile(uri, voiceName) { success ->
                                if (success) onDismiss()
                            }
                        }
                    },
                    isAnalyzing = isAnalyzingVoice
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RecordingTabContent(
    isRecording: Boolean,
    recordingDuration: Int,
    recordedTempFileExists: Boolean,
    isPlayingPreview: Boolean,
    permissionError: String?,
    onStartRecord: () -> Unit,
    onStopRecord: () -> Unit,
    onSave: () -> Unit,
    isAnalyzing: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseRecord")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (permissionError != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = RoseError.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = permissionError,
                    color = RoseError,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Timer Display
        val minutes = recordingDuration / 60
        val seconds = recordingDuration % 60
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = if (isRecording) RoseError else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isRecording) "جاري التسجيل... تحدّث بنص عربي واضح (5 - 30 ثانية)" else "اضغط على زر الميكروفون لبدء التسجيل الصوتي",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Big Record Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .scale(if (isRecording) pulseScale else 1f)
                .background(
                    color = if (isRecording) RoseError else MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .clickable {
                    if (isRecording) onStopRecord() else onStartRecord()
                }
                .testTag("record_audio_fab")
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isRecording) "إيقاف التسجيل" else "بدء التسجيل",
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save & Analyze Button
        Button(
            onClick = onSave,
            enabled = (isRecording || recordedTempFileExists) && !isAnalyzing,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_recorded_voice_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("جاري تحليل النبرة بالذكاء الاصطناعي...")
            } else {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRecording) "إيقاف وتحليل الصوت المخصص" else "تحليل وحفظ الصوت",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun UploadTabContent(
    selectedUri: Uri?,
    onPickFile: () -> Unit,
    onSave: () -> Unit,
    isAnalyzing: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onPickFile)
                .testTag("pick_audio_file_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AudioFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (selectedUri != null) "تم اختيار ملف صوتي بنجاح" else "اضغط لاختيار ملف صوتي من جهازك",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (selectedUri != null) selectedUri.lastPathSegment ?: "ملف صوتي" else "يدعم ملفات: MP3, WAV, M4A, AAC",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onSave,
            enabled = selectedUri != null && !isAnalyzing,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_uploaded_voice_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("جاري استيراد وتحليل النبرة بالذكاء الاصطناعي...")
            } else {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تحليل واستيراد الصوت المخصص", fontWeight = FontWeight.Bold)
            }
        }
    }
}
