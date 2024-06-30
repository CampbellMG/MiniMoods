package com.cmgcode.minimoods.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.data.MoodRepository
import com.cmgcode.minimoods.util.DateHelpers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeMoodRepository @Inject constructor() : MoodRepository {

    val moods = FilteredLiveMoods()
    override var shouldReportCrashes: Boolean? = null

    override suspend fun addMood(mood: Mood) {
        moods.value = moods.value
            ?.toMutableList()
            ?.apply {
                removeIf { it.date == mood.date }
                add(mood)
            }
    }

    override suspend fun getAllMoods(): List<Mood> {
        return moods.rawMoods.orEmpty()
    }

    override fun getMoodsForMonth(date: Date): LiveData<List<Mood>> {
        moods.date = date
        return moods
    }

    override suspend fun removeMood(date: Date) {
        moods.value = moods.value?.filter { it.date != date }
    }

    class FilteredLiveMoods : MutableLiveData<List<Mood>>() {
        var rawMoods: List<Mood>? = emptyList()

        var date: Date? = null
            set(value) {
                field = value
                this.value = rawMoods
            }

        init {
            value = rawMoods
        }

        override fun setValue(value: List<Mood>?) {
            rawMoods = value

            CoroutineScope(Dispatchers.Main).launch {
                super.setValue(filterList())
            }
        }

        private fun filterList(): List<Mood>? {
            return date?.let { date ->
                val monthRange = DateHelpers.getMonthRange(date)

                rawMoods?.filter {
                    it.date.time >= monthRange.first && it.date.time <= monthRange.second
                }
            } ?: rawMoods
        }
    }
}
