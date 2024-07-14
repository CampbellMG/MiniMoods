package com.cmgcode.minimoods.dependencies

import com.cmgcode.minimoods.data.MoodDao
import com.cmgcode.minimoods.data.MoodDatabase
import com.cmgcode.minimoods.data.PreferenceDao
import com.cmgcode.minimoods.handlers.error.ErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MiniMoodsModule::class]
)
class MockMiniMoodsModule {
    @Provides
    @Singleton
    fun providesErrorHandler(): ErrorHandler = mockk(relaxed = true)

    @Provides
    @Singleton
    fun providesDispatchers(): CoroutineDispatchers = CoroutineDispatchers(
        io = Dispatchers.Main,
        main = Dispatchers.Main
    )

    @Provides
    @Singleton
    fun providesMoodDao(): MoodDao = mockk(relaxed = true)

    @Provides
    @Singleton
    fun providesMoodDatabase(): MoodDatabase = mockk(relaxed = true)

    @Provides
    @Singleton
    fun providePreferenceDao(): PreferenceDao = mockk(relaxed = true)
}
