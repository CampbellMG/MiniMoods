package com.cmgcode.minimoods.moods

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgcode.minimoods.about.AboutActivity
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.data.MoodData
import com.cmgcode.minimoods.util.DateHelpers.isSameDay
import com.cmgcode.minimoods.util.Event
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MoodViewModel(
    private val repo: MoodData,
    private val IODispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    val navigationEvent = MutableLiveData<Event<Class<*>>?>()
    val exportEvent = MutableLiveData<Event<String>?>()
    val selectedDate = MutableLiveData(Date())

    val moods = switchMap(selectedDate) { repo.getMoodsForMonth(it) }

    val currentMood = map(moods) { monthMoods ->
        monthMoods
            .firstOrNull { mood -> selectedDate.value?.let { mood.date.isSameDay(it) } == true }
            ?.mood
    }

    fun addMood(moodScore: Int) {
        val date = selectedDate.value ?: Date()
        val mood = Mood(date, moodScore)

        viewModelScope.launch(IODispatcher) {
            repo.addMood(mood)
        }
    }

    fun export() {
        viewModelScope.launch(IODispatcher) {
            val moods = repo.getAllMoods()
            val data = moods.joinToString("\n") { "${it.date},${it.mood}" }
            viewModelScope.launch(Dispatchers.Main) { exportEvent.value = Event(data) }
        }
    }

    fun openAbout() {
        navigationEvent.value = Event(AboutActivity::class.java)
    }
}