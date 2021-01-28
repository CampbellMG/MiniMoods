package com.cmgcode.minimoods.dependencies

import android.content.Context
import com.cmgcode.minimoods.moods.MoodViewModel
import com.cmgcode.minimoods.data.MoodDatabase
import com.cmgcode.minimoods.data.MoodRepository

class AppContainer(context: Context): AppModule {
    private val database = MoodDatabase.getInstance(context)
    private val repository = MoodRepository(database.getMoodDao())

    override val moodViewModel = MoodViewModel(repository)
}
