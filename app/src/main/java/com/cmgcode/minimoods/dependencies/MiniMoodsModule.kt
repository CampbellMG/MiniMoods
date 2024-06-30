package com.cmgcode.minimoods.dependencies

import android.content.Context
import com.cmgcode.minimoods.data.MoodDao
import com.cmgcode.minimoods.data.MoodDatabase
import com.cmgcode.minimoods.data.MoodRepository
import com.cmgcode.minimoods.data.MoodRepositoryImpl
import com.cmgcode.minimoods.data.PreferenceDao
import com.cmgcode.minimoods.data.PreferenceDaoImpl
import com.cmgcode.minimoods.handlers.error.ErrorHandler
import com.cmgcode.minimoods.handlers.error.SentryErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class MiniMoodsModule {
    @Provides
    fun providesErrorHandler(prefs: PreferenceDao): ErrorHandler = SentryErrorHandler(prefs)

    @Provides
    fun providesDispatchers(): CoroutineDispatchers = CoroutineDispatchers()

    @Provides
    fun providesMoodDao(database: MoodDatabase): MoodDao = database.getMoodDao()

    @Provides
    fun providesMoodDatabase(@ApplicationContext context: Context): MoodDatabase =
        MoodDatabase.getInstance(context)

    @Provides
    fun provideMoodRepositoryImpl(impl: MoodRepositoryImpl): MoodRepository = impl

    @Provides
    fun providePreferencesDao(impl: PreferenceDaoImpl): PreferenceDao = impl
}