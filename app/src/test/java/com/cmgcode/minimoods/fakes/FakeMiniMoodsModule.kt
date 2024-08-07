package com.cmgcode.minimoods.fakes

import com.cmgcode.minimoods.data.MoodRepository
import com.cmgcode.minimoods.data.PreferenceDao
import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import com.cmgcode.minimoods.dependencies.MiniMoodsModule
import com.cmgcode.minimoods.handlers.error.ErrorHandler
import com.cmgcode.minimoods.moods.MoodSelectionUseCase
import com.cmgcode.minimoods.moods.MoodSelectionUseCaseImpl
import com.cmgcode.minimoods.util.UnconfinedCoroutineDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MiniMoodsModule::class]
)
object FakeMiniMoodsModule {
    @Provides
    fun provideMoodRepositoryImpl(impl: FakeMoodRepository): MoodRepository = impl

    @Provides
    fun providesPreferences(impl: FakePreferencesDao): PreferenceDao = impl

    @Provides
    fun providesErrorHandler(impl: FakeErrorHandler): ErrorHandler = impl

    @Provides
    fun providesDispatchers(): CoroutineDispatchers = UnconfinedCoroutineDispatchers()

    @Provides
    fun providesMoodSelectionUseCase(impl: MoodSelectionUseCaseImpl): MoodSelectionUseCase = impl
}