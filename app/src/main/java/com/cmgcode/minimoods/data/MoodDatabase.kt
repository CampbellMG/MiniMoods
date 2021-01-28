package com.cmgcode.minimoods.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cmgcode.minimoods.util.Constants.DATABASE_NAME
import com.cmgcode.minimoods.util.DataConverters

@Database(entities = [Mood::class], version = 1, exportSchema = false)
@TypeConverters(DataConverters::class)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun getMoodDao(): MoodDao

    companion object {
        @Volatile private var instance: MoodDatabase? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, MoodDatabase::class.java, DATABASE_NAME).build()
    }
}
