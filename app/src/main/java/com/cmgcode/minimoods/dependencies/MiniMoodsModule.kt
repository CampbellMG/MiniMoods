package com.cmgcode.minimoods.dependencies

import android.content.Context
import com.cmgcode.minimoods.data.MoodDao
import com.cmgcode.minimoods.data.MoodDatabase
import com.cmgcode.minimoods.data.PreferenceDao
import com.cmgcode.minimoods.handlers.error.ErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class MiniMoodsModule {
    @Provides
    fun providesErrorHandler(): ErrorHandler = ErrorHandler

    @Provides
    fun providesDispatchers(): CoroutineDispatchers = CoroutineDispatchers()

    @Provides
    fun providesMoodDatabase(@ApplicationContext context: Context): MoodDatabase =
        MoodDatabase.getInstance(context)

    @Provides
    fun providesMoodDao(database: MoodDatabase): MoodDao = database.getMoodDao()

    @Provides
    fun providesPreferenceDao(@ApplicationContext context: Context): PreferenceDao =
        PreferenceDao(context)
}