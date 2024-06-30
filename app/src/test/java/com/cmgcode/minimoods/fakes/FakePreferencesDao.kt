package com.cmgcode.minimoods.fakes

import com.cmgcode.minimoods.data.PreferenceDao

class FakePreferencesDao(
    override var shouldReportCrashes: Boolean? = null
) : PreferenceDao