package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomVoiceDao {
    @Query("SELECT * FROM custom_voices ORDER BY timestamp DESC")
    fun getAllCustomVoices(): Flow<List<CustomVoiceEntity>>

    @Query("SELECT * FROM custom_voices WHERE id = :id LIMIT 1")
    suspend fun getVoiceById(id: Long): CustomVoiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomVoice(voice: CustomVoiceEntity): Long

    @Update
    suspend fun updateCustomVoice(voice: CustomVoiceEntity)

    @Delete
    suspend fun deleteCustomVoice(voice: CustomVoiceEntity)

    @Query("DELETE FROM custom_voices WHERE id = :id")
    suspend fun deleteById(id: Long)
}
