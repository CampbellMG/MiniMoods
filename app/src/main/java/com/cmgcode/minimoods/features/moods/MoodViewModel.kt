package com.cmgcode.minimoods.features.moods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.data.MoodDao
import com.cmgcode.minimoods.data.PreferenceDao
import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import com.cmgcode.minimoods.handlers.error.ErrorHandler
import com.cmgcode.minimoods.util.DateRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

private data class MutableMoodState(
    val selectedDateRange: DateSelection,
    val errorLoggingPromptVisible: Boolean,
    val selectedMood: Mood?,
    val exportData: String?
)

data class MoodState(
    val selectedDateRange: DateSelection,
    val errorLoggingPromptVisible: Boolean,
    val moods: List<Mood>,
    val selectedMood: Mood?,
    val exportData: String?
)

sealed interface DateSelection {
    object ThisWeek : DateSelection
    object ThisMonth : DateSelection
    object Today : DateSelection
    data class Custom(val start: Date, val end: Date) : DateSelection
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MoodViewModel @Inject constructor(
    private val moodDao: MoodDao,
    private val preferenceDao: PreferenceDao,
    private val errorHandler: ErrorHandler,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {
    private val mutableState = MutableStateFlow(initialState())
    private val moods = mutableState.flatMapLatest {
        val (start, end) = it.selectedDateRange.toRange()
        moodDao.getMoodsBetween(start, end)
    }

    val state = combine(moods, mutableState) { moods, state -> mergeState(moods, state) }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            mergeState(emptyList(), mutableState.value)
        )

    fun addOrUpdatedMood(mood: Mood) {
        viewModelScope.launch(dispatchers.io) {
            moodDao.addOrUpdateMood(mood)
        }
    }

    fun export() {
        viewModelScope.launch(dispatchers.io) {
            val moods = moodDao.getAll()
            val data = moods.joinToString("\n") { "${it.date},${it.mood}" }

            mutableState.value = mutableState.value.copy(exportData = data)
        }
    }

    fun exportConsumed() {
        mutableState.value = mutableState.value.copy(exportData = null)
    }

    fun updateCrashReportingPreference(shouldReportCrashes: Boolean) {
        mutableState.value = mutableState.value.copy(errorLoggingPromptVisible = false)
        preferenceDao.shouldReportCrashes = shouldReportCrashes
        errorHandler.updateCrashReportingPreference(shouldReportCrashes)
    }

    fun selectDate(dateSelection: DateSelection) {
        mutableState.value = mutableState.value.copy(selectedDateRange = dateSelection)
    }

    fun selectMood(mood: Mood?) {
        mutableState.value = mutableState.value.copy(selectedMood = mood)
    }

    private fun DateSelection.toRange(): Pair<Long, Long> {
        return when (this) {
            is DateSelection.Custom -> start.time to end.time
            DateSelection.ThisMonth -> DateRange.thisMonth()
            DateSelection.ThisWeek -> DateRange.thisWeek()
            DateSelection.Today -> DateRange.today()
        }
    }

    private fun mergeState(moods: List<Mood>, mutableState: MutableMoodState): MoodState {
        return MoodState(
            selectedDateRange = mutableState.selectedDateRange,
            errorLoggingPromptVisible = mutableState.errorLoggingPromptVisible,
            selectedMood = mutableState.selectedMood,
            exportData = mutableState.exportData,
            moods = moods
        )
    }

    private fun initialState() = MutableMoodState(
        selectedDateRange = DateSelection.ThisMonth,
        errorLoggingPromptVisible = preferenceDao.shouldReportCrashes == null,
        selectedMood = null,
        exportData = null
    )
}
