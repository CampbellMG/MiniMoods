package com.cmgcode.minimoods.data

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.*

class MoodRepositoryTest {
    private lateinit var moodData: MoodDao
    private lateinit var repository: MoodRepository

    @Before
    fun setUp() {
        moodData = mockk(relaxed = true)
        repository = MoodRepository(moodData)
    }

    @Test
    fun when_RetrievingMoodsForMonth_Expect_StartAndEndDateForMonth() {
        val start = Calendar.getInstance()
            .apply {
                set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))

                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            .time.time

        val end = Calendar.getInstance()
            .apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))

                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            .time.time

        repository.getMoodsForMonth(Date())

        verify { moodData.getMoodsBetween(start, end) }
    }

    @Test
    fun when_AddingMood_Expect_NoTimeOnlyDate() {
        val date = Date()
        val mood = Mood(date, 1)

        repository.addMood(mood)

        val time = Calendar.getInstance()
            .apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            .time

        verify { moodData.addMood(Mood(time, 1)) }
    }
}
