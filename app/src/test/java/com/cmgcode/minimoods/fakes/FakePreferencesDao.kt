package com.cmgcode.minimoods.fakes

import com.cmgcode.minimoods.data.PreferenceDao
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakePreferencesDao(
    override var shouldReportCrashes: MutableStateFlow<Boolean?> = MutableStateFlow(null)
) : PreferenceDao {

    @Inject
    constructor() : this(MutableStateFlow(null))

    override suspend fun updateCrashReporting(value: Boolean?) {
        shouldReportCrashes.value = value
    }
}