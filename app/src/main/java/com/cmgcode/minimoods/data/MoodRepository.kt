package com.cmgcode.minimoods.data

import androidx.lifecycle.LiveData
import com.cmgcode.minimoods.util.DateHelpers
import com.cmgcode.minimoods.util.DateHelpers.atStartOfDay
import java.util.*
import javax.inject.Inject

class MoodRepository @Inject constructor(
    private val moods: MoodDao,
    private val preferences: PreferenceDao
) {

    var shouldReportCrashes: Boolean?
        get() = preferences.shouldReportCrashes
        set(value) {
            preferences.shouldReportCrashes = value
        }

    fun addMood(mood: Mood) {
        val startOfDate = Calendar.getInstance()
            .apply { time = mood.date }
            .atStartOfDay()
            .time

        val validatedMood = mood.copy(date = startOfDate)

        moods.addMood(validatedMood)
    }

    fun getAllMoods(): List<Mood> {
        return moods.getAll()
    }

    fun getMoodsForMonth(date: Date): LiveData<List<Mood>> {
        val monthRange = DateHelpers.getMonthRange(date)
        return moods.getMoodsBetween(monthRange.first, monthRange.second)
    }
}
