package com.cmgcode.minimoods.mock

import com.cmgcode.minimoods.handlers.error.ErrorHandler

class MockErrorHandler: ErrorHandler {
    var shouldLog: Boolean? = null

    override fun updateCrashReportingPreference(shouldLog: Boolean) {
        this.shouldLog = shouldLog
    }
}
