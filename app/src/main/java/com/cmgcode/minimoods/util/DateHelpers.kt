package com.cmgcode.minimoods.util

import java.util.*
import java.util.Calendar.*

object DateHelpers {
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
