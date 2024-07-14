package com.cmgcode.minimoods.util

import java.util.*

object DateRange {
    fun today(): Pair<Long, Long> {
        return getRangeFromNow()
    }

    fun thisWeek(): Pair<Long, Long> {
        return getRangeFromNow(
            { set(Calendar.DAY_OF_WEEK, getActualMinimum(Calendar.DAY_OF_WEEK)) },
            { set(Calendar.DAY_OF_WEEK, getActualMaximum(Calendar.DAY_OF_WEEK)) }
        )
    }

    fun thisMonth(): Pair<Long, Long> {
        return getRangeFromNow(
            { set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH)) },
            { set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) }
        )
    }

    private fun getRangeFromNow(
        startMutation: Calendar.() -> Unit = {},
        endMutation: Calendar.() -> Unit = {}
    ): Pair<Long, Long> {
        val start = Calendar.getInstance()
            .apply { startMutation() }
            .atStartOfDay()
            .time.time

        val end = Calendar.getInstance()
            .apply { endMutation() }
            .atEndOfDay()
            .time.time

        return start to end
    }

    private fun Calendar.atStartOfDay(): Calendar {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        return this
    }

    private fun Calendar.atEndOfDay(): Calendar {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)

        return this
    }
}
