package com.example.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class AudioRecorderManager(private val context: Context) {
    private val tag = "AudioRecorderManager"
    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0)
    val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()

    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> = _amplitude.asStateFlow()

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun startRecording(): Boolean {
        try {
            stopRecording()

            val dir = File(context.filesDir, "custom_voices")
            if (!dir.exists()) dir.mkdirs()

            val file = File(dir, "record_${System.currentTimeMillis()}.m4a")
            currentOutputFile = file

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            _isRecording.value = true
            _recordingDurationSeconds.value = 0

            startTimer()
            Log.d(tag, "Recording started: ${file.absolutePath}")
            return true
        } catch (e: Exception) {
            Log.e(tag, "Failed to start recording", e)
            _isRecording.value = false
            currentOutputFile = null
            return false
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            var seconds = 0
            while (isActive && _isRecording.value) {
                delay(100)
                try {
                    val amp = mediaRecorder?.maxAmplitude ?: 0
                    _amplitude.value = amp
                } catch (e: Exception) {
                    // ignore
                }

                if (seconds % 10 == 0) {
                    _recordingDurationSeconds.value = seconds / 10
                }
                seconds++
            }
        }
    }

    fun stopRecording(): File? {
        timerJob?.cancel()
        timerJob = null
        _isRecording.value = false
        _amplitude.value = 0

        val recorder = mediaRecorder ?: return currentOutputFile
        return try {
            recorder.stop()
            recorder.reset()
            recorder.release()
            mediaRecorder = null
            val file = currentOutputFile
            Log.d(tag, "Recording stopped successfully. File size: ${file?.length() ?: 0} bytes")
            file
        } catch (e: Exception) {
            Log.e(tag, "Error stopping recorder", e)
            mediaRecorder = null
            currentOutputFile
        }
    }

    fun cancelRecording() {
        timerJob?.cancel()
        timerJob = null
        _isRecording.value = false
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaRecorder = null
        currentOutputFile?.delete()
        currentOutputFile = null
    }
}
