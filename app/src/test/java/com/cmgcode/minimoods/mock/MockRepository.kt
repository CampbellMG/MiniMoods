package com.cmgcode.minimoods.mock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.data.MoodData
import com.cmgcode.minimoods.util.DateHelpers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MockRepository : MoodData {
    val moods = FilteredLiveMoods()

    override fun addMood(mood: Mood) {
        moods.value = moods.value
            ?.toMutableList()
            ?.apply {
                removeIf { it.date == mood.date }
                add(mood)
            }
    }
    override fun getAllMoods(): List<Mood> {
        return moods.value.orEmpty()
    }

    override fun getMoodsForMonth(date: Date): LiveData<List<Mood>> {
        moods.date = date
        return moods
    }

    class FilteredLiveMoods : MutableLiveData<List<Mood>>() {
        private var rawMoods: List<Mood>? = emptyList()

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
