package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "speech_history")
data class SpeechHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalText: String,
    val vocalizedText: String,
    val pitch: Float,
    val speechRate: Float,
    val toneName: String,
    val voiceName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
