package com.cmgcode.minimoods.moods

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.mock.MockErrorHandler
import com.cmgcode.minimoods.mock.MockRepository
import com.cmgcode.minimoods.util.MainCoroutineRule
import com.cmgcode.minimoods.util.getTestValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.robolectric.annotation.LooperMode
import java.util.*
import java.util.Calendar.*

@ExperimentalCoroutinesApi
@LooperMode(LooperMode.Mode.PAUSED)
class MoodViewModelTest {
    @get:Rule var coroutineRule = MainCoroutineRule()
    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: MockRepository
    private lateinit var errorHandler: MockErrorHandler
    private lateinit var viewModel: MoodViewModel

    @Before
    fun setUp() {
        repository = MockRepository()
        errorHandler = MockErrorHandler()
        viewModel = MoodViewModel(repository, errorHandler, Dispatchers.Main)
    }

    @Test
    fun when_Initialised_Expect_SelectedMonthIsFromToday() {
        val selectedMonth = getInstance()
            .apply { time = viewModel.selectedDate.getTestValue() }

        assertThat(selectedMonth.get(MONTH))
            .isEqualTo(getInstance().get(MONTH))
    }

    @Test
    fun when_SelectedDateChanges_Expect_MoodsToUpdate() {
        val nextMonth = getInstance().apply { add(MONTH, 1) }.time
        val thisMonthMood = Mood(Date(), 1)
        val nextMonthMood = Mood(nextMonth, 1)

        repository.addMood(thisMonthMood)
        repository.addMood(nextMonthMood)

        assertThat(viewModel.moods.getTestValue())
            .contains(thisMonthMood)

        viewModel.selectedDate.value = nextMonth

        assertThat(viewModel.moods.getTestValue())
            .contains(nextMonthMood)
    }

    @Test
    fun when_AddingMood_Expect_CorrectMoodAndCurrentDate() {
        val score = 1
        val tomorrow = getInstance().apply { add(DATE, 1) }.time

        viewModel.selectedDate.value = tomorrow
        viewModel.addMood(score)

        val mood = repository.moods.getTestValue().first()

        assertThat(mood.date).isEqualTo(tomorrow)
        assertThat(mood.mood).isEqualTo(score)
    }

    @Test
    fun when_Exporting_Expect_ExportEventWithMoodDataInCSVFormat() {
        val nextMonth = getInstance().apply { add(MONTH, 1) }.time
        val thisMonthMood = Mood(Date(), 1)
        val nextMonthMood = Mood(nextMonth, 1)
        val data = listOf(thisMonthMood, nextMonthMood).joinToString("\n") { "${it.date},${it.mood}" }

        repository.addMood(thisMonthMood)
        repository.addMood(nextMonthMood)

        viewModel.export()

        assertThat(viewModel.exportEvent.getTestValue()?.unhandledData)
            .isEqualTo(data)
    }

    @Test
    fun when_CurrentMoodRetrieved_Expect_ToMatchSelectedDateMood() {
        val tomorrow = getInstance().apply { add(DATE, 1) }.time
        val mood = Mood(tomorrow, 1)

        repository.addMood(mood)

        viewModel.selectedDate.value = tomorrow

        assertThat(viewModel.currentMood.getTestValue())
            .isEqualTo(mood.mood)
    }

    @Test
    fun when_SettingShouldLog_Expect_RepoToUpdate() {
        viewModel.updateCrashReportingPreference(true)

        assertThat(repository.shouldReportCrashes).isTrue()
    }

    @Test
    fun when_SettingShouldLog_Expect_ErrorHandlerToUpdate() {
        viewModel.updateCrashReportingPreference(false)

        assertThat(errorHandler.shouldLog).isFalse()
    }

    @Test
    fun when_SettingShouldLogToNull_Expect_ErrorHandlerToNotLog() {
        viewModel.updateCrashReportingPreference(true)

        assertThat(errorHandler.shouldLog).isTrue()

        viewModel.updateCrashReportingPreference(null)

        assertThat(errorHandler.shouldLog).isFalse()
    }
}
