package com.cmgcode.minimoods.util

import androidx.room.TypeConverter
import java.util.*

class DataConverters {
    @TypeConverter fun fromTimestamp(value: Long?) = value?.let { Date(it) }
    @TypeConverter fun toTimestamp(value: Date?) = value?.time
}
