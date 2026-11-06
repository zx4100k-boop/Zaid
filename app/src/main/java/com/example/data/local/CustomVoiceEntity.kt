package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_voices")
data class CustomVoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val voiceName: String,
    val sourcePath: String,
    val durationSeconds: Int,
    val pitch: Float,
    val speechRate: Float,
    val toneLabel: String,
    val voiceGender: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
