package com.cmgcode.minimoods.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Mood(
    @PrimaryKey val date: Long,
    val mood: Int,
    val comment: String? = null
)
