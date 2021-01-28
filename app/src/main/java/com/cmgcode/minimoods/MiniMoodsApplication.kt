package com.cmgcode.minimoods

import android.app.Application
import com.cmgcode.minimoods.dependencies.AppContainer
import com.cmgcode.minimoods.dependencies.AppModule

class MiniMoodsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        module = AppContainer(applicationContext)
    }

    companion object {
        lateinit var module: AppModule
    }
}
