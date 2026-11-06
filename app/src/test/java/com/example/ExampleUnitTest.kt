package com.example

import com.example.data.ai.VoiceTone
import com.example.data.local.CustomVoiceEntity
import com.example.data.makharij.MakharijRepository
import com.example.data.makharij.MakhrajRegion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun makharijRepository_containsAllRegionsAndLetters() {
        val allLetters = MakharijRepository.allLetters
        assertFalse("Letters list should not be empty", allLetters.isEmpty())
        
        MakhrajRegion.entries.forEach { region ->
            val regionLetters = MakharijRepository.getLettersByRegion(region)
            assertTrue("Region ${region.name} should have letters", regionLetters.isNotEmpty())
        }
    }

    @Test
    fun voiceTones_haveValidDefaultPitchesAndRates() {
        VoiceTone.entries.forEach { tone ->
            assertTrue("Pitch should be in valid range", tone.defaultPitch in 0.5f..2.0f)
            assertTrue("Rate should be in valid range", tone.defaultRate in 0.5f..2.0f)
            assertTrue("Title should be in Arabic", tone.titleAr.isNotBlank())
        }
    }

    @Test
    fun customVoiceEntity_createsCorrectModel() {
        val voice = CustomVoiceEntity(
            voiceName = "صوت وثائقي مميز",
            sourcePath = "/data/audio/sample.m4a",
            durationSeconds = 12,
            pitch = 0.85f,
            speechRate = 0.90f,
            toneLabel = "صوت جهوري وقور",
            voiceGender = "رجالي"
        )
        assertEquals("صوت وثائقي مميز", voice.voiceName)
        assertEquals(0.85f, voice.pitch, 0.001f)
        assertEquals(0.90f, voice.speechRate, 0.001f)
    }
}
