package com.cmgcode.minimoods.mock

import com.cmgcode.minimoods.dependencies.AppModule
import com.cmgcode.minimoods.moods.MoodViewModel
import kotlinx.coroutines.Dispatchers

class MockContainer : AppModule {
    val repository = MockRepository()
    private val errorHandler = MockErrorHandler()

    override val moodViewModel = MoodViewModel(repository, errorHandler, Dispatchers.Main)
}
