package com.cmgcode.minimoods.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.data.MoodDao

class FakeMoodDao(
    val moods: MutableList<Mood> = mutableListOf()
) : MoodDao {

    override suspend fun getAll(): List<Mood> = moods

    override fun getMoodsBetween(from: Long, to: Long): LiveData<List<Mood>> {
        val result = MutableLiveData<List<Mood>>()
        result.value = moods.filter { it.date.time in from..to }
        return result
    }

    override suspend fun addMood(mood: Mood) {
        moods.add(mood)
    }

    override suspend fun deleteMood(date: Long) {
        moods.removeAll { it.date.time == date }
    }

    override suspend fun getMood(date: Long): Mood? {
        return moods.firstOrNull { it.date.time == date }
    }
}