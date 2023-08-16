package com.cmgcode.minimoods.mock

class MockErrorHandler: ErrorHandler {
    var shouldLog: Boolean? = null

    override fun updateCrashReportingPreference(shouldLog: Boolean) {
        this.shouldLog = shouldLog
    }
}
