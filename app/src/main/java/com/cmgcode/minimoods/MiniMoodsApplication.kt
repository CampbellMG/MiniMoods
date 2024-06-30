package com.cmgcode.minimoods

import android.app.Application
import com.cmgcode.minimoods.handlers.error.ErrorHandler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MiniMoodsApplication : Application() {
    @Inject
    lateinit var errorHandler: ErrorHandler

    override fun onCreate() {
        super.onCreate()

        errorHandler.init()
    }
}
