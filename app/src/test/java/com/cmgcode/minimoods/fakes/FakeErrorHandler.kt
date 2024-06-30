package com.cmgcode.minimoods.fakes

import com.cmgcode.minimoods.handlers.error.ErrorHandler

class FakeErrorHandler(
    var shouldLog: Boolean? = null
) : ErrorHandler {

    override fun updateCrashReportingPreference(shouldLog: Boolean) {
        this.shouldLog = shouldLog
    }
}
