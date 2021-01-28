package com.cmgcode.minimoods.data

import androidx.lifecycle.LiveData
import java.util.*

interface MoodData {
    fun addMood(mood: Mood)

    fun getAllMoods(): List<Mood>

    fun getMoodsForMonth(date: Date): LiveData<List<Mood>>
}
