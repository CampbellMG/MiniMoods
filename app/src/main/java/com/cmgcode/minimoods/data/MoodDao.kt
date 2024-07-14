package com.cmgcode.minimoods.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood")
    fun getAll(): List<Mood>

    @Query("SELECT * FROM mood WHERE date BETWEEN :from AND :to")
    fun getMoodsBetween(from: Long, to: Long): Flow<List<Mood>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateMood(mood: Mood)
}
