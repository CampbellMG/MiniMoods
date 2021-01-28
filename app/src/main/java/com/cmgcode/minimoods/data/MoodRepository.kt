package com.cmgcode.minimoods.data

import androidx.lifecycle.LiveData
import com.cmgcode.minimoods.util.DateHelpers
import com.cmgcode.minimoods.util.DateHelpers.atStartOfDay
import java.util.*

class MoodRepository(private val moods: MoodDao): MoodData {

    override fun addMood(mood: Mood) {
        val startOfDate = Calendar.getInstance()
            .apply { time = mood.date }
            .atStartOfDay()
            .time

        val validatedMood = mood.copy(date = startOfDate)

        moods.addMood(validatedMood)
    }

    override fun getAllMoods(): List<Mood> {
        return moods.getAll()
    }

    override fun getMoodsForMonth(date: Date): LiveData<List<Mood>> {
        val monthRange = DateHelpers.getMonthRange(date)
        return moods.getMoodsBetween(monthRange.first, monthRange.second)
    }
}
