package com.cmgcode.minimoods.moods

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.data.MoodData
import com.cmgcode.minimoods.handlers.error.ErrorHandler
import com.cmgcode.minimoods.util.DateHelpers.isSameDay
import com.cmgcode.minimoods.util.Event
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class MoodViewModel(
    private val repo: MoodData,
    private val errorHandler: ErrorHandler,
    private val IODispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    val exportEvent = MutableLiveData<Event<String>?>()
    val selectedDate = MutableLiveData(Date())
    val shouldReportCrashes = MutableLiveData(repo.shouldReportCrashes)

    val moods = selectedDate.switchMap {  repo.getMoodsForMonth(it) }

    val currentMood = moods.map { monthMoods ->
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

    fun updateCrashReportingPreference(shouldReportCrashes: Boolean?) {
        this.shouldReportCrashes.value = shouldReportCrashes
        repo.shouldReportCrashes = shouldReportCrashes

        errorHandler.updateCrashReportingPreference(shouldReportCrashes ?: false)
    }
}
