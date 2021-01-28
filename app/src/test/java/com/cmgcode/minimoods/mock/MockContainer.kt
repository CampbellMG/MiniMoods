package com.cmgcode.minimoods.mock

import com.cmgcode.minimoods.moods.MoodViewModel
import com.cmgcode.minimoods.dependencies.AppModule
import kotlinx.coroutines.Dispatchers

class MockContainer : AppModule {
    val repository = MockRepository()

    override val moodViewModel = MoodViewModel(repository, Dispatchers.Main)
}
