package com.cmgcode.minimoods.moods

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.data.MoodRepository
import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import com.cmgcode.minimoods.handlers.error.ErrorHandler
import com.cmgcode.minimoods.util.DateHelpers.isSameDay
import com.cmgcode.minimoods.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val repo: MoodRepository,
    private val errorHandler: ErrorHandler,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    val exportEvent = MutableLiveData<Event<String>?>()
    val selectedDate = MutableLiveData(Date())
    val shouldReportCrashes = MutableLiveData(repo.shouldReportCrashes)

    val moods = selectedDate.switchMap { repo.getMoodsForMonth(it) }

    val currentMood = moods.map { monthMoods ->
        monthMoods
            .firstOrNull { mood -> selectedDate.value?.let { mood.date.isSameDay(it) } == true }
            ?.mood
    }

    fun addMood(moodScore: Int) {
        val date = selectedDate.value ?: Date()
        val mood = Mood(date, moodScore)

        viewModelScope.launch(dispatchers.io) {
            repo.addMood(mood)
        }
    }

    fun export() {
        viewModelScope.launch(dispatchers.io) {
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
