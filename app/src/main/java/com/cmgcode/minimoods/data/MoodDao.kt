package com.cmgcode.minimoods.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood")
    suspend fun getAll(): List<Mood>

    @Query("SELECT * FROM mood WHERE date = :date")
    suspend fun getMood(date: Long): Mood?

    @Query("SELECT * FROM mood WHERE date BETWEEN :from AND :to")
    fun getMoodsBetween(from: Long, to: Long): LiveData<List<Mood>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMood(mood: Mood)

    @Query("DELETE FROM mood WHERE date = :date")
    suspend fun deleteMood(date: Long)
}
