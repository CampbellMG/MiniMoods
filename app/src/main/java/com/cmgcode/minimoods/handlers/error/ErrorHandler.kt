package com.cmgcode.minimoods.handlers.error

import com.cmgcode.minimoods.BuildConfig
import io.sentry.Sentry

object ErrorHandler {
    fun updateCrashReportingPreference(shouldLog: Boolean) {
        Sentry.init {
            it.dsn = if (shouldLog) BuildConfig.SENTRY_DSN else ""
        }
    }
}
