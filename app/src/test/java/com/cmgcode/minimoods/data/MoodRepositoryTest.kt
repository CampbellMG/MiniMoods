package com.cmgcode.minimoods.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import com.cmgcode.minimoods.fakes.FakeMoodDao
import com.cmgcode.minimoods.util.DateHelpers
import com.cmgcode.minimoods.util.DateHelpers.atStartOfDay
import com.cmgcode.minimoods.util.DateHelpers.toCalendar
import com.cmgcode.minimoods.util.TestCoroutineDispatchers
import com.cmgcode.minimoods.util.mood
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.Calendar
import java.util.Date

class MoodRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `WHEN adding mood EXPECT mood added with time at start of day`() = runTest {
        // GIVEN
        val moodDao = FakeMoodDao()
        val repo = moodRepository(moodDao = moodDao)
        val mood = mood(date = Date(), mood = 1)

        // WHEN
        repo.addMood(mood)

        // THEN
        val startOfDay = mood.date.toCalendar().atStartOfDay().time
        assertThat(moodDao.moods).hasSize(1)
        assertThat(moodDao.moods.first()).isEqualTo(mood.copy(date = startOfDay))
    }

    @Test
    fun `WHEN retrieving mood EXPECT dao data`() = runTest {
        // GIVEN
        val moods = mutableListOf(mood(date = Date(), mood = 1))
        val moodDao = FakeMoodDao(moods)
        val repo = moodRepository(moodDao = moodDao)

        // WHEN
        val result = repo.getAllMoods()

        // THEN
        assertThat(result).isEqualTo(moods)
    }

    @Test
    fun `WHEN retrieving moods for month EXPECT moods in range`() {
        // GIVEN
        val date = Date()
        val (start, end) = DateHelpers.getMonthRange(date)
        val moods = listOf(
            mood(date = Date(start - 1)),
            mood(date = Date(start)),
            mood(date = Date(start + 1)),
            mood(date = Date(end - 1)),
            mood(date = Date(end)),
            mood(date = Date(end + 1)),
        )

        val moodDao = FakeMoodDao(moods.toMutableList())
        val repo = moodRepository(moodDao = moodDao)

        // WHEN
        val result = repo.getMoodsForMonth(date)

        // THEN
        assertThat(result.value).isEqualTo(moods.subList(1, 5))
    }

    @Test
    fun `WHEN removing mood EXPECT mood removed`() = runTest {
        // GIVEN
        val date = Calendar.getInstance()
            .apply { time = Date() }
            .atStartOfDay()
            .time

        val moodDao = FakeMoodDao(mutableListOf(mood(date = date)))
        val repo = moodRepository(moodDao = moodDao)

        // WHEN
        repo.removeMood(date)

        // THEN
        assertThat(moodDao.moods).isEmpty()
    }

    private fun moodRepository(
        moodDao: MoodDao = FakeMoodDao(),
        dispatchers: CoroutineDispatchers = TestCoroutineDispatchers()
    ): MoodRepository {
        return MoodRepositoryImpl(moodDao, dispatchers)
    }
}
