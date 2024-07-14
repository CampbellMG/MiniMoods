package com.cmgcode.minimoods.moods

import app.cash.turbine.test
import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.data.MoodDao
import com.cmgcode.minimoods.data.PreferenceDao
import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import com.cmgcode.minimoods.features.moods.DateSelection
import com.cmgcode.minimoods.features.moods.MoodViewModel
import com.cmgcode.minimoods.handlers.error.ErrorHandler
import com.cmgcode.minimoods.util.DateRange
import com.cmgcode.minimoods.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class MoodViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moodDao: MoodDao
    private lateinit var preferenceDao: PreferenceDao
    private lateinit var errorHandler: ErrorHandler
    private lateinit var viewModel: MoodViewModel

    @Before
    fun setUp() {
        moodDao = mockk(relaxed = true)
        preferenceDao = mockk(relaxed = true)
        errorHandler = mockk(relaxed = true)

        every { moodDao.getMoodsBetween(any(), any()) } returns flowOf(emptyList())

        viewModel = buildViewModel()
    }


    @Test
    fun `WHEN initialised EXPECT this month date selection`() = runTest {
        viewModel.state.test {
            assertThat(awaitItem().selectedDateRange)
                .isEqualTo(DateSelection.ThisMonth)
        }
    }

    @Test
    fun `WHEN initialised EXPECT moods empty`() = runTest {
        viewModel.state.test {
            assertThat(awaitItem().moods)
                .isEmpty()
        }
    }

    @Test
    fun `WHEN initialised EXPECT selected mood null`() = runTest {
        viewModel.state.test {
            assertThat(awaitItem().selectedMood)
                .isNull()
        }
    }

    @Test
    fun `WHEN initialised EXPECT this month's moods are retrieved`() = runTest {
        val (start, end) = DateRange.thisMonth()
        val thisMonthMood = Mood(Date(), 1)

        every { moodDao.getMoodsBetween(start, end) } returns
                flow { emit(listOf(thisMonthMood)) }

        viewModel = buildViewModel()

        viewModel.state.test {
            assertThat(awaitItem().moods).contains(thisMonthMood)
        }
    }

    @Test
    fun `WHEN initialised with null reporting preference EXPECT prompt visible`() = runTest {
        every { preferenceDao.shouldReportCrashes } returns null

        viewModel = buildViewModel()

        viewModel.state.test {
            assertThat(awaitItem().errorLoggingPromptVisible)
                .isTrue()
        }
    }

    @Test
    fun `WHEN initialised with false reporting preference EXPECT prompt visible`() = runTest {
        every { preferenceDao.shouldReportCrashes } returns false

        viewModel = buildViewModel()

        viewModel.state.test {
            assertThat(awaitItem().errorLoggingPromptVisible)
                .isFalse()
        }
    }

    @Test
    fun `WHEN initialised with true reporting preference EXPECT prompt visible`() = runTest {
        every { preferenceDao.shouldReportCrashes } returns true

        viewModel = buildViewModel()

        viewModel.state.test {
            assertThat(awaitItem().errorLoggingPromptVisible)
                .isFalse()
        }
    }

    @Test
    fun `WHEN selecting date EXPECT correct date in state`() = runTest {
        every { moodDao.getMoodsBetween(any(), any()) } returns flowOf(emptyList())

        val date = Date()

        viewModel.selectDate(DateSelection.Custom(date, date))

        viewModel.state.test {
            assertThat(awaitItem().selectedDateRange)
                .isEqualTo(DateSelection.Custom(date, date))
        }
    }

    @Test
    fun `WHEN selected today EXPECT today's moods are retrieved`() = runTest {
        val (start, end) = DateRange.today()
        val todayMood = Mood(Date(), 1)

        every { moodDao.getMoodsBetween(start, end) } returns
                flow { emit(listOf(todayMood)) }

        viewModel.selectDate(DateSelection.Today)

        viewModel.state.test {
            assertThat(awaitItem().moods).contains(todayMood)
        }
    }

    @Test
    fun `WHEN selected this week EXPECT this week's moods are retrieved`() = runTest {
        val (start, end) = DateRange.thisWeek()
        val thisWeeksMood = Mood(Date(), 1)

        every { moodDao.getMoodsBetween(start, end) } returns
                flow { emit(listOf(thisWeeksMood)) }

        viewModel.selectDate(DateSelection.ThisWeek)

        viewModel.state.test {
            assertThat(awaitItem().moods).contains(thisWeeksMood)
        }
    }

    @Test
    fun `WHEN selected custom date range EXPECT matching moods are retrieved`() = runTest {
        val (start, end) = Date() to Date()
        val customRangeMood = Mood(Date(), 1)

        every { moodDao.getMoodsBetween(start.time, end.time) } returns
                flow { emit(listOf(customRangeMood)) }

        viewModel.selectDate(DateSelection.Custom(start, end))

        viewModel.state.test {
            assertThat(awaitItem().moods).contains(customRangeMood)
        }
    }

    @Test
    fun `WHEN updating reporting preference to true EXPECT prompt not visible`() = runTest {
        every { preferenceDao.shouldReportCrashes } returns null

        viewModel = buildViewModel()

        viewModel.updateCrashReportingPreference(true)

        viewModel.state.test {
            assertThat(awaitItem().errorLoggingPromptVisible)
                .isFalse()
        }
    }

    @Test
    fun `WHEN updating reporting preference to false EXPECT prompt not visible`() = runTest {
        every { preferenceDao.shouldReportCrashes } returns null

        viewModel = buildViewModel()

        viewModel.updateCrashReportingPreference(false)

        viewModel.state.test {
            assertThat(awaitItem().errorLoggingPromptVisible)
                .isFalse()
        }
    }

    @Test
    fun `WHEN updating reporting preference EXPECT DAO and error handler updated`() = runTest {
        val newReportingPreference = true

        viewModel.updateCrashReportingPreference(newReportingPreference)

        verify { preferenceDao.shouldReportCrashes = newReportingPreference }
        verify { errorHandler.updateCrashReportingPreference(newReportingPreference) }
    }

    @Test
    fun `WHEN adding mood EXPECT DAO updated`() = runTest {
        val mood = Mood(Date(), 1)

        viewModel.addOrUpdatedMood(mood)

        verify { moodDao.addOrUpdateMood(mood) }
    }

    @Test
    fun `WHEN exporting and consuming EXPECT data in CSV format then cleared`() = runTest {
        val nextMonth = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }.time
        val mood1 = Mood(Date(), 1)
        val mood2 = Mood(nextMonth, 1)
        val data = listOf(mood1, mood2)
            .joinToString("\n") { "${it.date},${it.mood}" }

        every { moodDao.getAll() } returns listOf(mood1, mood2)

        viewModel.export()

        viewModel.state.test {
            val state = awaitItem()

            assertThat(state.exportData)
                .isEqualTo(data)

            viewModel.exportConsumed()

            val consumedState = awaitItem()

            assertThat(consumedState.exportData)
                .isNull()
        }
    }

    @Test
    fun `WHEN selecting mood EXPECT selected mood in state`() = runTest {
        val mood = Mood(Date(), 1)

        viewModel.selectMood(mood)

        viewModel.state.test {
            assertThat(awaitItem().selectedMood)
                .isEqualTo(mood)
        }
    }

    @Test
    fun `WHEN clearing selecting mood EXPECT selected mood in state is null`() = runTest {
        val mood = Mood(Date(), 1)

        viewModel.selectMood(mood)
        viewModel.selectMood(null)

        viewModel.state.test {
            assertThat(awaitItem().selectedMood)
                .isNull()
        }
    }

    private fun buildViewModel() =
        MoodViewModel(moodDao, preferenceDao, errorHandler, CoroutineDispatchers(Dispatchers.Main))
}
