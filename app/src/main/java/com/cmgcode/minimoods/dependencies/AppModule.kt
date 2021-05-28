package com.cmgcode.minimoods.dependencies

import com.cmgcode.minimoods.moods.MoodViewModel
import com.cmgcode.minimoods.util.ViewModelFactory

interface AppModule {
    val moodViewModelFactory: ViewModelFactory<MoodViewModel>
}
