package com.cmgcode.minimoods.handlers.error

interface ErrorHandler {
    fun updateCrashReportingPreference(shouldLog: Boolean)
}
