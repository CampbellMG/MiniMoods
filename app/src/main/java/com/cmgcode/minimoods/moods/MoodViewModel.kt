package com.cmgcode.minimoods.moods

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.cmgcode.minimoods.data.MoodRepository
import com.cmgcode.minimoods.data.PreferenceDao
import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import com.cmgcode.minimoods.util.DateHelpers.isSameDay
import com.cmgcode.minimoods.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val repo: MoodRepository,
    private val preferences: PreferenceDao,
    private val selectMood: MoodSelectionUseCase,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {
    val exportEvent = MutableLiveData<Event<String>?>()
    val selectedDate = MutableLiveData(Date())
    val shouldReportCrashes = preferences.shouldReportCrashes.asLiveData()

    val moods = selectedDate.switchMap { repo.getMoodsForMonth(it) }

    val currentMood = moods.map { monthMoods ->
        monthMoods
            .firstOrNull { mood -> selectedDate.value?.let { mood.date.isSameDay(it) } == true }
            ?.mood
    }

    fun toggleMood(moodScore: Int) {
        val date = selectedDate.value ?: Date()

        viewModelScope.launch {
            selectMood(date, moodScore)
        }
    }

    fun export() {
        viewModelScope.launch {
            val moods = repo.getAllMoods()
            val data = moods.joinToString("\n") { "${it.date},${it.mood}" }
            viewModelScope.launch(dispatchers.main) { exportEvent.value = Event(data) }
        }
    }

    fun updateCrashReportingPreference(shouldReportCrashes: Boolean?) {
        viewModelScope.launch {
            preferences.updateCrashReporting(shouldReportCrashes)
        }
    }
}
