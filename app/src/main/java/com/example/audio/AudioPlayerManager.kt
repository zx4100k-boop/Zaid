package com.example.audio

import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class AudioPlayerManager {
    private val tag = "AudioPlayerManager"
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentlyPlayingPath = MutableStateFlow<String?>(null)
    val currentlyPlayingPath: StateFlow<String?> = _currentlyPlayingPath.asStateFlow()

    fun play(filePath: String, onCompletion: (() -> Unit)? = null) {
        val file = File(filePath)
        if (!file.exists()) {
            Log.e(tag, "Audio file does not exist: $filePath")
            return
        }

        stop()

        try {
            val player = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentlyPlayingPath.value = null
                    onCompletion?.invoke()
                }
                start()
            }
            mediaPlayer = player
            _isPlaying.value = true
            _currentlyPlayingPath.value = filePath
        } catch (e: Exception) {
            Log.e(tag, "Error playing audio file", e)
            _isPlaying.value = false
            _currentlyPlayingPath.value = null
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.w(tag, "Error releasing player", e)
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentlyPlayingPath.value = null
    }

    fun release() {
        stop()
    }
}
