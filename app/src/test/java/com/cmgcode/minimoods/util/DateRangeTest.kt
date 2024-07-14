package com.cmgcode.minimoods.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Calendar


class DateRangeTest {
    @Test
    fun `WHEN getting month range for date EXPECT start of first day and end of last day`() {
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

        val result = DateRange.thisMonth()

        assertThat(result.first).isEqualTo(start)
        assertThat(result.second).isEqualTo(end)
    }

    @Test
    fun `WHEN getting day range for date EXPECT start of day and end of day`() {
        val start = Calendar.getInstance()
            .apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            .time.time

        val end = Calendar.getInstance()
            .apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            .time.time

        val result = DateRange.today()

        assertThat(result.first).isEqualTo(start)
        assertThat(result.second).isEqualTo(end)
    }

    @Test
    fun `WHEN getting week range for date EXPECT start of week and end of week`() {
        val start = Calendar.getInstance()
            .apply {
                set(Calendar.DAY_OF_WEEK, getActualMinimum(Calendar.DAY_OF_WEEK))
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            .time.time

        val end = Calendar.getInstance()
            .apply {
                set(Calendar.DAY_OF_WEEK, getActualMaximum(Calendar.DAY_OF_WEEK))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            .time.time

        val result = DateRange.thisWeek()

        assertThat(result.first).isEqualTo(start)
        assertThat(result.second).isEqualTo(end)
    }
}