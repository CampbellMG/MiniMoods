package com.cmgcode.minimoods.util

import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MILLISECOND
import java.util.Calendar.MINUTE
import java.util.Calendar.MONTH
import java.util.Calendar.SECOND
import java.util.Calendar.YEAR
import java.util.Calendar.getInstance
import java.util.Date

object DateHelpers {
    fun getFirstDayOfPreviousMonth(date: Date): Date {
        val firstDayOfLastMonth = date
            .toCalendar()
            .apply {
                add(MONTH, -1)
                set(DAY_OF_MONTH, getActualMinimum(DAY_OF_MONTH))
            }

        return firstDayOfLastMonth.time
    }

    fun getFirstDayOfNextMonth(date: Date): Date {
        val firstDayOfLastMonth = date
            .toCalendar()
            .apply {
                add(MONTH, 1)
                set(DAY_OF_MONTH, getActualMinimum(DAY_OF_MONTH))
            }

        return firstDayOfLastMonth.time
    }

    fun getMonthRange(date: Date): Pair<Long, Long> {
        val start = getInstance()
            .apply {
                time = date
                set(DAY_OF_MONTH, getActualMinimum(DAY_OF_MONTH))
            }
            .atStartOfDay()
            .time.time

        val end = getInstance()
            .apply {
                time = date
                set(DAY_OF_MONTH, getActualMaximum(DAY_OF_MONTH))
            }
            .atEndOfDay()
            .time.time

        return start to end
    }

    fun Date.toCalendar(): Calendar = getInstance().also { it.time = this }

    fun Date.isToday(): Boolean = isSameDay(Date())

    fun Date.isSameDay(date: Date): Boolean {
        val calendar = getInstance().also { it.time = this }
        val target = date.toCalendar()

        return calendar.get(DAY_OF_YEAR) == target.get(DAY_OF_YEAR)
                && calendar.get(YEAR) == target.get( YEAR)
    }

    fun Calendar.atStartOfDay(): Calendar {
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)

        return this
    }

    private fun Calendar.atEndOfDay(): Calendar {

        set(HOUR_OF_DAY, 23)
        set(MINUTE, 59)
        set(SECOND, 59)
        set(MILLISECOND, 999)

        return this
    }
}
