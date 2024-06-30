package com.cmgcode.minimoods.handlers.error

import com.cmgcode.minimoods.BuildConfig
import io.sentry.Sentry

interface ErrorHandler {
    fun updateCrashReportingPreference(shouldLog: Boolean)
}

object SentryErrorHandler : ErrorHandler {
    override fun updateCrashReportingPreference(shouldLog: Boolean) {
        Sentry.init {
            it.dsn = if (shouldLog) BuildConfig.SENTRY_DSN else ""
        }
    }
}
