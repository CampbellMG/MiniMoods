package com.cmgcode.minimoods.mock

import com.cmgcode.minimoods.dependencies.AppModule
import com.cmgcode.minimoods.moods.MoodViewModel
import com.cmgcode.minimoods.util.ViewModelFactory
import kotlinx.coroutines.Dispatchers

class MockContainer : AppModule {
    val repository = MockRepository()
    private val errorHandler = MockErrorHandler()

    override val moodViewModelFactory = object : ViewModelFactory<MoodViewModel> {
        override fun create() = MoodViewModel(repository, errorHandler, Dispatchers.Main)
    }
}
