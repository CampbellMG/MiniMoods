package com.cmgcode.minimoods.data

import androidx.lifecycle.LiveData
import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import com.cmgcode.minimoods.util.DateHelpers
import com.cmgcode.minimoods.util.DateHelpers.atStartOfDay
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

interface MoodRepository {
    var shouldReportCrashes: Boolean?

    suspend fun addMood(mood: Mood)
    suspend fun getAllMoods(): List<Mood>
    fun getMoodsForMonth(date: Date): LiveData<List<Mood>>
    suspend fun removeMood(date: Date)
}

class MoodRepositoryImpl @Inject constructor(
    private val moods: MoodDao,
    private val preferences: PreferenceDao,
    private val dispatchers: CoroutineDispatchers
) : MoodRepository {

    override var shouldReportCrashes: Boolean?
        get() = preferences.shouldReportCrashes
        set(value) {
            preferences.shouldReportCrashes = value
        }

    override suspend fun addMood(mood: Mood) {
        val startOfDate = Calendar.getInstance()
            .apply { time = mood.date }
            .atStartOfDay()
            .time

        val validatedMood = mood.copy(date = startOfDate)

        withContext(dispatchers.io) {
            moods.addMood(validatedMood)
        }
    }

    override suspend fun getAllMoods(): List<Mood> {
        return withContext(dispatchers.io) {
            moods.getAll()
        }
    }

    override fun getMoodsForMonth(date: Date): LiveData<List<Mood>> {
        val monthRange = DateHelpers.getMonthRange(date)
        return moods.getMoodsBetween(monthRange.first, monthRange.second)
    }

    override suspend fun removeMood(date: Date) {
        val startOfDate = Calendar.getInstance()
            .apply { time = date }
            .atStartOfDay()
            .time

        return withContext(dispatchers.io) {
            moods.deleteMood(date = startOfDate.time)
        }
    }
}
