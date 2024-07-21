package com.cmgcode.minimoods.moods

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cmgcode.minimoods.fakes.FakeMoodRepository
import com.cmgcode.minimoods.util.MainCoroutineRule
import com.cmgcode.minimoods.util.getTestValue
import com.cmgcode.minimoods.util.mood
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.Date

@ExperimentalCoroutinesApi
class MoodSelectionUseCaseImplTest {

    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `WHEN no existing mood EXPECT mood added to repo`() = runTest {
        // GIVEN
        val repo = FakeMoodRepository()
        val selectMood = MoodSelectionUseCaseImpl(repo)
        val mood = mood(date = Date(), mood = 0)

        // WHEN
        selectMood(mood.date, mood.mood)

        // THEN
        val result = repo.moods.getTestValue().first()

        assertThat(result).isEqualTo(mood)
    }

    @Test
    fun `WHEN mood exists with same value EXPECT mood removed`() = runTest {
        // GIVEN
        val mood = mood(date = Date(), mood = 0)
        val repo = FakeMoodRepository().also {
            it.moods.value = mutableListOf(mood)
        }

        val selectMood = MoodSelectionUseCaseImpl(repo)

        // WHEN
        selectMood(mood.date, mood.mood)

        // THEN
        val result = repo.moods.getTestValue()

        assertThat(result).isEmpty()
    }

    @Test
    fun `WHEN mood exists with new value EXPECT mood removed`() = runTest {
        // GIVEN
        val mood = mood(date = Date(), mood = 0)
        val newValue = 1
        val repo = FakeMoodRepository().also {
            it.moods.value = mutableListOf(mood)
        }

        val selectMood = MoodSelectionUseCaseImpl(repo)

        // WHEN
        selectMood(mood.date, newValue)

        // THEN
        val result = repo.moods.getTestValue()?.first()
        val expectedMood = mood.copy(mood = newValue)

        assertThat(result).isEqualTo(expectedMood)
    }
}