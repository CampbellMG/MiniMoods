package com.cmgcode.minimoods.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

fun Long.toDate() = Date().apply { time = this@toDate }
fun Date.toCalendar() = Calendar.getInstance().also { it.time = this }

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}
