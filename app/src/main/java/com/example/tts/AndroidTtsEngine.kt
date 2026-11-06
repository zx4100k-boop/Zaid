package com.example.tts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID

sealed interface TtsPlaybackState {
    data object Idle : TtsPlaybackState
    data object Initializing : TtsPlaybackState
    data class Speaking(val text: String, val utteranceId: String) : TtsPlaybackState
    data class Error(val message: String) : TtsPlaybackState
    data object Finished : TtsPlaybackState
}

data class AvailableVoice(
    val name: String,
    val locale: Locale,
    val isNetworkConnectionRequired: Boolean,
    val latency: Int,
    val quality: Int,
    val voiceObj: Voice?
)

class AndroidTtsEngine(private val context: Context) : TextToSpeech.OnInitListener {
    private val tag = "AndroidTtsEngine"
    private var textToSpeech: TextToSpeech? = null

    private val _playbackState = MutableStateFlow<TtsPlaybackState>(TtsPlaybackState.Initializing)
    val playbackState: StateFlow<TtsPlaybackState> = _playbackState.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _availableArabicVoices = MutableStateFlow<List<AvailableVoice>>(emptyList())
    val availableArabicVoices: StateFlow<List<AvailableVoice>> = _availableArabicVoices.asStateFlow()

    private val _selectedVoiceName = MutableStateFlow<String?>(null)
    val selectedVoiceName: StateFlow<String?> = _selectedVoiceName.asStateFlow()

    private var currentPitch: Float = 1.0f
    private var currentRate: Float = 1.0f
    private var currentUtteranceId: String? = null

    init {
        initEngine()
    }

    fun initEngine() {
        try {
            _playbackState.value = TtsPlaybackState.Initializing
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(tag, "Failed to instantiate TextToSpeech", e)
            _playbackState.value = TtsPlaybackState.Error("تعذر تشغيل محرك تحويل النص إلى صوت: ${e.localizedMessage}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val tts = textToSpeech ?: return
            val arabicLocale = Locale("ar", "SA")
            val arabicGeneric = Locale("ar")

            var langResult = tts.setLanguage(arabicLocale)
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                langResult = tts.setLanguage(arabicGeneric)
            }

            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(tag, "Arabic TTS language might need voice pack or network connection")
            }

            tts.setPitch(currentPitch)
            tts.setSpeechRate(currentRate)

            loadAvailableVoices(tts)

            setupProgressListener(tts)

            _isInitialized.value = true
            _playbackState.value = TtsPlaybackState.Idle
            Log.d(tag, "TTS initialized successfully with Arabic support")
        } else {
            Log.e(tag, "TextToSpeech init failed with status: $status")
            _playbackState.value = TtsPlaybackState.Error("فشل تهيئة محرك الصوت. تأكد من تفعيل خدمة الصوت في النظام.")
        }
    }

    private fun loadAvailableVoices(tts: TextToSpeech) {
        try {
            val allVoices = tts.voices
            if (!allVoices.isNullOrEmpty()) {
                val arabicVoices = allVoices.filter { voice ->
                    val lang = voice.locale.language.lowercase(Locale.ROOT)
                    lang == "ar"
                }.map { voice ->
                    AvailableVoice(
                        name = voice.name,
                        locale = voice.locale,
                        isNetworkConnectionRequired = voice.isNetworkConnectionRequired,
                        latency = voice.latency,
                        quality = voice.quality,
                        voiceObj = voice
                    )
                }

                _availableArabicVoices.value = arabicVoices
                if (arabicVoices.isNotEmpty()) {
                    val defaultVoice = arabicVoices.first()
                    _selectedVoiceName.value = defaultVoice.name
                    tts.voice = defaultVoice.voiceObj
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "Could not query available voices", e)
        }
    }

    private fun setupProgressListener(tts: TextToSpeech) {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                val currentText = (playbackState.value as? TtsPlaybackState.Speaking)?.text ?: ""
                _playbackState.value = TtsPlaybackState.Speaking(currentText, utteranceId ?: "")
            }

            override fun onDone(utteranceId: String?) {
                _playbackState.value = TtsPlaybackState.Finished
            }

            override fun onError(utteranceId: String?) {
                _playbackState.value = TtsPlaybackState.Error("حدث خطأ أثناء نطق النص الصوتي.")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                val errorMsg = when (errorCode) {
                    TextToSpeech.ERROR_NETWORK -> "خطأ في الاتصال بالشبكة لنطق الصوت."
                    TextToSpeech.ERROR_NETWORK_TIMEOUT -> "انتهت مهلة الاتصال بالشبكة."
                    TextToSpeech.ERROR_NOT_INSTALLED_YET -> "حزمة الصوت العربي قيد التثبيت."
                    TextToSpeech.ERROR_SYNTHESIS -> "خطأ في توليد التردد الصوتي."
                    else -> "خطأ في تشغيل الصوت (رمز $errorCode)."
                }
                Log.e(tag, "TTS error on utterance: $utteranceId, code: $errorCode")
                _playbackState.value = TtsPlaybackState.Error(errorMsg)
            }
        })
    }

    fun setPitch(pitch: Float) {
        currentPitch = pitch
        textToSpeech?.setPitch(pitch)
    }

    fun setSpeechRate(rate: Float) {
        currentRate = rate
        textToSpeech?.setSpeechRate(rate)
    }

    fun selectVoice(voiceName: String) {
        val voiceItem = _availableArabicVoices.value.find { it.name == voiceName }
        if (voiceItem?.voiceObj != null) {
            textToSpeech?.voice = voiceItem.voiceObj
            _selectedVoiceName.value = voiceName
        }
    }

    fun speak(text: String) {
        val tts = textToSpeech
        if (tts == null) {
            initEngine()
            return
        }

        if (text.isBlank()) return

        stop()

        val utteranceId = UUID.randomUUID().toString()
        currentUtteranceId = utteranceId
        _playbackState.value = TtsPlaybackState.Speaking(text, utteranceId)

        tts.setPitch(currentPitch)
        tts.setSpeechRate(currentRate)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        try {
            textToSpeech?.stop()
            _playbackState.value = TtsPlaybackState.Idle
        } catch (e: Exception) {
            Log.e(tag, "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (e: Exception) {
            Log.e(tag, "Error shutting down TTS", e)
        }
    }
}
