package com.cmgcode.minimoods.util

import com.cmgcode.minimoods.data.Mood
import java.util.Date

fun mood(
    date: Date = Date(),
    mood: Int = 0
) = Mood(date, mood)