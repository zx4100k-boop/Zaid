package com.example.ui.viewmodel

import android.app.Application
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerManager
import com.example.audio.AudioRecorderManager
import com.example.data.ai.AiVoiceService
import com.example.data.ai.MakharijAnalysisResult
import com.example.data.ai.VoiceTone
import com.example.data.local.AppDatabase
import com.example.data.local.CustomVoiceEntity
import com.example.data.local.SpeechHistoryEntity
import com.example.data.makharij.ArabicLetterMakhraj
import com.example.tts.AndroidTtsEngine
import com.example.tts.AvailableVoice
import com.example.tts.TtsPlaybackState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "MainViewModel"
    private val database = AppDatabase.getDatabase(application)
    private val historyDao = database.speechHistoryDao()
    private val customVoiceDao = database.customVoiceDao()

    val ttsEngine = AndroidTtsEngine(application)
    private val aiService = AiVoiceService()

    // Audio Recorder & Preview Player for Custom Voices
    val recorderManager = AudioRecorderManager(application)
    val playerManager = AudioPlayerManager()

    val isRecording: StateFlow<Boolean> = recorderManager.isRecording
    val recordingDurationSeconds: StateFlow<Int> = recorderManager.recordingDurationSeconds
    val recordingAmplitude: StateFlow<Int> = recorderManager.amplitude
    val isPlayingPreview: StateFlow<Boolean> = playerManager.isPlaying
    val currentlyPlayingPreviewPath: StateFlow<String?> = playerManager.currentlyPlayingPath

    // Texts
    private val _inputText = MutableStateFlow(
        "إِنَّ اللُّغَةَ العَرَبِيَّةَ لُغَةُ الضَّادِ، تَتَمَيَّزُ بِفَصَاحَةِ بَيَانِهَا وَدِقَّةِ مَخَارِجِ حُرُوفِهَا."
    )
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _vocalizedText = MutableStateFlow("")
    val vocalizedText: StateFlow<String> = _vocalizedText.asStateFlow()

    // Audio Controls
    private val _pitch = MutableStateFlow(1.0f)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _selectedTone = MutableStateFlow(VoiceTone.NATURAL)
    val selectedTone: StateFlow<VoiceTone> = _selectedTone.asStateFlow()

    // Custom Voices List from Room
    val customVoices: StateFlow<List<CustomVoiceEntity>> = customVoiceDao.getAllCustomVoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCustomVoice = MutableStateFlow<CustomVoiceEntity?>(null)
    val selectedCustomVoice: StateFlow<CustomVoiceEntity?> = _selectedCustomVoice.asStateFlow()

    private val _isAnalyzingVoice = MutableStateFlow(false)
    val isAnalyzingVoice: StateFlow<Boolean> = _isAnalyzingVoice.asStateFlow()

    // AI Operation States
    private val _isDiacritizing = MutableStateFlow(false)
    val isDiacritizing: StateFlow<Boolean> = _isDiacritizing.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _isEnhancingTone = MutableStateFlow(false)
    val isEnhancingTone: StateFlow<Boolean> = _isEnhancingTone.asStateFlow()

    private val _analysisResult = MutableStateFlow<MakharijAnalysisResult?>(null)
    val analysisResult: StateFlow<MakharijAnalysisResult?> = _analysisResult.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // History from Room
    val historyItems: StateFlow<List<SpeechHistoryEntity>> = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playbackState: StateFlow<TtsPlaybackState> = ttsEngine.playbackState
    val availableVoices: StateFlow<List<AvailableVoice>> = ttsEngine.availableArabicVoices
    val selectedVoiceName: StateFlow<String?> = ttsEngine.selectedVoiceName

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun setPitch(newPitch: Float) {
        val clamped = newPitch.coerceIn(0.5f, 2.0f)
        _pitch.value = clamped
        ttsEngine.setPitch(clamped)
    }

    fun setSpeechRate(newRate: Float) {
        val clamped = newRate.coerceIn(0.5f, 2.0f)
        _speechRate.value = clamped
        ttsEngine.setSpeechRate(clamped)
    }

    fun selectTone(tone: VoiceTone) {
        _selectedCustomVoice.value = null
        _selectedTone.value = tone
        setPitch(tone.defaultPitch)
        setSpeechRate(tone.defaultRate)
    }

    fun selectVoice(name: String) {
        _selectedCustomVoice.value = null
        ttsEngine.selectVoice(name)
    }

    fun selectCustomVoice(voice: CustomVoiceEntity) {
        _selectedCustomVoice.value = voice
        setPitch(voice.pitch)
        setSpeechRate(voice.speechRate)
        _userMessage.value = "تم تفعيل الصوت المخصص: ${voice.voiceName}"
    }

    fun clearCustomVoiceSelection() {
        _selectedCustomVoice.value = null
        setPitch(1.0f)
        setSpeechRate(1.0f)
    }

    // --- Custom Voice Recording Actions ---

    fun startRecording(): Boolean {
        playerManager.stop()
        return recorderManager.startRecording()
    }

    fun stopRecordingAndSave(voiceName: String, onFinished: (Boolean) -> Unit) {
        val recordedFile = recorderManager.stopRecording()
        val duration = recorderManager.recordingDurationSeconds.value.coerceAtLeast(1)

        if (recordedFile == null || !recordedFile.exists() || recordedFile.length() == 0L) {
            _userMessage.value = "تعذر حفظ التسجيل الصوتي. حاول مرة أخرى."
            onFinished(false)
            return
        }

        viewModelScope.launch {
            _isAnalyzingVoice.value = true
            _userMessage.value = "جاري تحليل البصمة الصوتية بالذكاء الاصطناعي ومطابقة النبرة..."
            try {
                val profile = aiService.analyzeCustomVoiceProfile(voiceName, duration)
                val entity = CustomVoiceEntity(
                    voiceName = voiceName.ifBlank { "صوتي المسجل" },
                    sourcePath = recordedFile.absolutePath,
                    durationSeconds = duration,
                    pitch = profile.recommendedPitch,
                    speechRate = profile.recommendedRate,
                    toneLabel = profile.toneLabel,
                    voiceGender = profile.gender,
                    notes = profile.description
                )
                val id = customVoiceDao.insertCustomVoice(entity)
                val savedVoice = entity.copy(id = id)
                selectCustomVoice(savedVoice)
                _userMessage.value = "تمت إضافة الصوت المخصص وتحليل نبرته بنجاح!"
                onFinished(true)
            } catch (e: Exception) {
                Log.e(tag, "Error saving recorded voice", e)
                _userMessage.value = "تم حفظ الصوت مع الإعدادات الافتراضية للنبرة."
                onFinished(false)
            } finally {
                _isAnalyzingVoice.value = false
            }
        }
    }

    fun cancelRecording() {
        recorderManager.cancelRecording()
    }

    // --- Custom Voice Audio File Import Actions ---

    fun importAudioFile(uri: Uri, voiceName: String, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isAnalyzingVoice.value = true
            _userMessage.value = "جاري استيراد وتحليل الملف الصوتي بالذكاء الاصطناعي..."
            try {
                val context = getApplication<Application>()
                val targetDir = File(context.filesDir, "custom_voices")
                if (!targetDir.exists()) targetDir.mkdirs()

                val targetFile = File(targetDir, "imported_${System.currentTimeMillis()}.m4a")

                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }

                if (!targetFile.exists() || targetFile.length() == 0L) {
                    throw IllegalStateException("فشل نسخ الملف الصوتي المختار.")
                }

                // Determine duration via MediaMetadataRetriever
                val durationSec = withContext(Dispatchers.IO) {
                    try {
                        val mmr = MediaMetadataRetriever()
                        mmr.setDataSource(targetFile.absolutePath)
                        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        mmr.release()
                        ((durationStr?.toLongOrNull() ?: 5000L) / 1000L).toInt().coerceAtLeast(1)
                    } catch (e: Exception) {
                        5
                    }
                }

                val finalVoiceName = voiceName.ifBlank { "صوت مستورد" }
                val profile = aiService.analyzeCustomVoiceProfile(finalVoiceName, durationSec)

                val entity = CustomVoiceEntity(
                    voiceName = finalVoiceName,
                    sourcePath = targetFile.absolutePath,
                    durationSeconds = durationSec,
                    pitch = profile.recommendedPitch,
                    speechRate = profile.recommendedRate,
                    toneLabel = profile.toneLabel,
                    voiceGender = profile.gender,
                    notes = profile.description
                )

                val id = customVoiceDao.insertCustomVoice(entity)
                val savedVoice = entity.copy(id = id)
                selectCustomVoice(savedVoice)
                _userMessage.value = "تم استيراد الصوت ومطابقة نبرته بنجاح!"
                onFinished(true)
            } catch (e: Exception) {
                Log.e(tag, "Error importing audio file", e)
                _userMessage.value = "تعذر استيراد الملف الصوتي: ${e.localizedMessage}"
                onFinished(false)
            } finally {
                _isAnalyzingVoice.value = false
            }
        }
    }

    fun playPreviewAudio(filePath: String) {
        ttsEngine.stop()
        playerManager.play(filePath)
    }

    fun stopPreviewAudio() {
        playerManager.stop()
    }

    fun deleteCustomVoice(voice: CustomVoiceEntity) {
        viewModelScope.launch {
            if (_selectedCustomVoice.value?.id == voice.id) {
                clearCustomVoiceSelection()
            }
            playerManager.stop()
            try {
                File(voice.sourcePath).delete()
            } catch (e: Exception) {
                // ignore
            }
            customVoiceDao.deleteCustomVoice(voice)
            _userMessage.value = "تم حذف الصوت المخصص."
        }
    }

    // --- AI Smart Operations ---

    fun requestSmartDiacritization() {
        val current = _inputText.value.trim()
        if (current.isBlank()) {
            _userMessage.value = "يرجى كتابة نص لتشكيله."
            return
        }

        viewModelScope.launch {
            _isDiacritizing.value = true
            _userMessage.value = "جاري تشكيل النص بدقة بالذكاء الاصطناعي لضبط مخارج الحروف..."
            try {
                val result = aiService.diacritizeText(current)
                _inputText.value = result
                _vocalizedText.value = result
                _userMessage.value = "تم تشكيل النص بدقة وضبط الحركات بنجاح!"
            } catch (e: Exception) {
                _userMessage.value = e.localizedMessage ?: "فشل التشكيل الذكي."
            } finally {
                _isDiacritizing.value = false
            }
        }
    }

    fun requestMakharijAnalysis() {
        val current = _inputText.value.trim()
        if (current.isBlank()) {
            _userMessage.value = "يرجى كتابة نص لتحليله."
            return
        }

        viewModelScope.launch {
            _isAnalyzing.value = true
            _userMessage.value = "جاري تحليل مخارج الحروف والنبرة الصوتية..."
            try {
                val result = aiService.analyzePhonetics(current)
                _analysisResult.value = result
                _userMessage.value = "تم تدقيق مخارج الحروف الصوتية بنجاح!"
            } catch (e: Exception) {
                _userMessage.value = e.localizedMessage ?: "فشل تحليل مخارج الحروف."
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun enhanceTextForCurrentTone() {
        val current = _inputText.value.trim()
        if (current.isBlank()) return

        viewModelScope.launch {
            _isEnhancingTone.value = true
            _userMessage.value = "جاري تكييف النص وإيقاع الوقف لنبرة ${_selectedTone.value.titleAr}..."
            try {
                val enhanced = aiService.enhanceForTone(current, _selectedTone.value)
                _inputText.value = enhanced
                _userMessage.value = "تمت ملاءمة فواصل الإلقاء لنبرة ${_selectedTone.value.titleAr}."
            } catch (e: Exception) {
                _userMessage.value = e.localizedMessage ?: "فشل ضبط نبرة النص."
            } finally {
                _isEnhancingTone.value = false
            }
        }
    }

    fun speakCurrentText() {
        val textToSpeak = _inputText.value.trim()
        if (textToSpeak.isBlank()) {
            _userMessage.value = "يرجى إدخال نص أولاً للبدء بالنطق."
            return
        }

        playerManager.stop()
        ttsEngine.setPitch(_pitch.value)
        ttsEngine.setSpeechRate(_speechRate.value)
        ttsEngine.speak(textToSpeak)

        // Automatically save to local history
        viewModelScope.launch {
            val currentCustom = _selectedCustomVoice.value
            val effectiveTone = currentCustom?.let { "صوت مخصص: ${it.voiceName}" } ?: _selectedTone.value.titleAr
            val effectiveVoice = currentCustom?.voiceName ?: selectedVoiceName.value ?: "افتراضي"

            historyDao.insertItem(
                SpeechHistoryEntity(
                    originalText = textToSpeak,
                    vocalizedText = _vocalizedText.value.ifBlank { textToSpeak },
                    pitch = _pitch.value,
                    speechRate = _speechRate.value,
                    toneName = effectiveTone,
                    voiceName = effectiveVoice
                )
            )
        }
    }

    fun stopSpeaking() {
        ttsEngine.stop()
    }

    fun speakLetterPhonetic(letter: ArabicLetterMakhraj, phrase: String) {
        playerManager.stop()
        ttsEngine.setPitch(_pitch.value)
        ttsEngine.setSpeechRate(0.80f)
        ttsEngine.speak(phrase)
    }

    fun loadHistoryItem(item: SpeechHistoryEntity) {
        _inputText.value = item.originalText
        _vocalizedText.value = item.vocalizedText
        setPitch(item.pitch)
        setSpeechRate(item.speechRate)
        val matchedTone = VoiceTone.entries.find { it.titleAr == item.toneName } ?: VoiceTone.NATURAL
        _selectedTone.value = matchedTone
        _userMessage.value = "تم استرجاع النص وإعدادات النبرة."
    }

    fun toggleFavorite(item: SpeechHistoryEntity) {
        viewModelScope.launch {
            historyDao.updateItem(item.copy(isFavorite = !item.isFavorite))
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyDao.deleteById(id)
            _userMessage.value = "تم حذف التسجيل من السجل."
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyDao.clearAll()
            _userMessage.value = "تم مسح جميع التسجيلات."
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun removeDiacritics() {
        val raw = _inputText.value
        val stripped = raw.replace(Regex("[\u064B-\u0652\u0670]"), "")
        _inputText.value = stripped
        _userMessage.value = "تمت إزالة الحركات والتشكيل."
    }

    override fun onCleared() {
        super.onCleared()
        ttsEngine.shutdown()
        recorderManager.stopRecording()
        playerManager.release()
    }
}
