package com.cmgcode.minimoods.moods

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.data.MoodRepository
import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import com.cmgcode.minimoods.fakes.FakeErrorHandler
import com.cmgcode.minimoods.fakes.FakeMoodRepository
import com.cmgcode.minimoods.handlers.error.ErrorHandler
import com.cmgcode.minimoods.util.MainCoroutineRule
import com.cmgcode.minimoods.util.UnconfinedCoroutineDispatchers
import com.cmgcode.minimoods.util.getTestValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.robolectric.annotation.LooperMode
import java.util.Calendar.DATE
import java.util.Calendar.MONTH
import java.util.Calendar.getInstance
import java.util.Date

@ExperimentalCoroutinesApi
@LooperMode(LooperMode.Mode.PAUSED)
class MoodViewModelTest {
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `WHEN initialised EXPECT selected month is from today`() {
        // GIVEN
        val viewModel = moodViewModel()

        // WHEN
        val selectedMonth = getInstance()
            .apply { time = viewModel.selectedDate.getTestValue() }

        // THEN
        assertThat(selectedMonth.get(MONTH))
            .isEqualTo(getInstance().get(MONTH))
    }

    @Test
    fun `WHEN selected date changes EXPECT moods to update`() = runTest {
        // GIVEN
        val repository = FakeMoodRepository()
        val viewModel = moodViewModel(repo = repository)
        val nextMonth = getInstance().apply { add(MONTH, 1) }.time
        val thisMonthMood = Mood(Date(), 1)
        val nextMonthMood = Mood(nextMonth, 1)

        // WHEN
        repository.addMood(thisMonthMood)
        repository.addMood(nextMonthMood)

        // THEN
        assertThat(viewModel.moods.getTestValue())
            .contains(thisMonthMood)

        // WHEN
        viewModel.selectedDate.value = nextMonth

        // THEN
        assertThat(viewModel.moods.getTestValue())
            .contains(nextMonthMood)
    }

    @Test
    fun `WHEN adding mood EXPECT correct mood and current date`() {
        // GIVEN
        val score = 1
        val repository = FakeMoodRepository()
        val viewModel = moodViewModel(repo = repository)
        val tomorrow = getInstance().apply { add(DATE, 1) }.time

        // WHEN
        viewModel.selectedDate.value = tomorrow
        viewModel.toggleMood(score)

        // THEN
        val mood = repository.moods.getTestValue().first()

        assertThat(mood.date).isEqualTo(tomorrow)
        assertThat(mood.mood).isEqualTo(score)
    }

    @Test
    fun `WHEN exporting EXPECT export event with mood data in CSV format`() = runTest {
        // GIVEN
        val repository = FakeMoodRepository()
        val viewModel = moodViewModel(repo = repository)
        val nextMonth = getInstance().apply { add(MONTH, 1) }.time
        val thisMonthMood = Mood(Date(), 1)
        val nextMonthMood = Mood(nextMonth, 1)
        val data =
            listOf(thisMonthMood, nextMonthMood).joinToString("\n") { "${it.date},${it.mood}" }

        // WHEN
        repository.addMood(thisMonthMood)
        repository.addMood(nextMonthMood)

        viewModel.export()

        // THEN
        assertThat(viewModel.exportEvent.getTestValue()?.unhandledData)
            .isEqualTo(data)
    }

    @Test
    fun `WHEN current mood retrieved EXPECT to match selected date mood`() = runTest {
        // GIVEN
        val repository = FakeMoodRepository()
        val viewModel = moodViewModel(repo = repository)
        val tomorrow = getInstance().apply { add(DATE, 1) }.time
        val mood = Mood(tomorrow, 1)

        // WHEN
        repository.addMood(mood)

        viewModel.selectedDate.value = tomorrow

        // THEN
        assertThat(viewModel.currentMood.getTestValue())
            .isEqualTo(mood.mood)
    }

    @Test
    fun `WHEN setting should log EXPECT repo to update`() {
        // GIVEN
        val repository = FakeMoodRepository()
        val viewModel = moodViewModel(repo = repository)

        // WHEN
        viewModel.updateCrashReportingPreference(true)

        // THEN
        assertThat(repository.shouldReportCrashes).isTrue()
    }

    @Test
    fun `WHEN setting should log EXPECT error handler to update`() {
        // GIVEN
        val errorHandler = FakeErrorHandler()
        val viewModel = moodViewModel(errorHandler = errorHandler)

        // WHEN
        viewModel.updateCrashReportingPreference(false)

        // THEN
        assertThat(errorHandler.shouldLog).isFalse()
    }

    @Test
    fun `WHEN setting should log to null EXPECT error handler to not log`() {
        // GIVEN
        val errorHandler = FakeErrorHandler()
        val viewModel = moodViewModel(errorHandler = errorHandler)

        // WHEN
        viewModel.updateCrashReportingPreference(true)

        // THEN
        assertThat(errorHandler.shouldLog).isTrue()

        // WHEN
        viewModel.updateCrashReportingPreference(null)

        // THEN
        assertThat(errorHandler.shouldLog).isFalse()
    }


    private fun moodViewModel(
        repo: MoodRepository = FakeMoodRepository(),
        errorHandler: ErrorHandler = FakeErrorHandler(),
        dispatchers: CoroutineDispatchers = UnconfinedCoroutineDispatchers()
    ) = MoodViewModel(repo, errorHandler, dispatchers)
}
