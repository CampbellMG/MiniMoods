package com.cmgcode.minimoods.dependencies

import android.content.Context
import com.cmgcode.minimoods.data.MoodDatabase
import com.cmgcode.minimoods.data.MoodRepository
import com.cmgcode.minimoods.data.PreferenceDao
import com.cmgcode.minimoods.handlers.error.SentryErrorHandler
import com.cmgcode.minimoods.moods.MoodViewModel

class AppContainer(context: Context): AppModule {
    private val preferences = PreferenceDao(context)
    private val database = MoodDatabase.getInstance(context)
    private val repository = MoodRepository(database.getMoodDao(), preferences)

    override val moodViewModel = MoodViewModel(repository, SentryErrorHandler)
}
