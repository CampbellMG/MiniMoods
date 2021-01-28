package com.cmgcode.minimoods.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cmgcode.minimoods.R
import java.util.*

@Entity
data class Mood(
    @PrimaryKey val date: Date,
    val mood: Int
) {
    fun getInvertedMood() = 5 - mood + 1

    fun getMoodColor() = when (mood) {
        1 -> R.color.colorMood1
        2 -> R.color.colorMood2
        3 -> R.color.colorMood3
        4 -> R.color.colorMood4
        5 -> R.color.colorMood5
        else -> R.color.colorBackground
    }
}
