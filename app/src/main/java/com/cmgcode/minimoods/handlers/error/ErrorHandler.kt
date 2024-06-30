package com.cmgcode.minimoods.handlers.error

import com.cmgcode.minimoods.BuildConfig
import com.cmgcode.minimoods.data.PreferenceDao
import io.sentry.Sentry

interface ErrorHandler {
    fun updateCrashReportingPreference(shouldLog: Boolean)
    fun init()
}

class SentryErrorHandler(private val prefs: PreferenceDao) : ErrorHandler {
    override fun init() {
        if (prefs.shouldReportCrashes == true) {
            Sentry.init {
                it.dsn = BuildConfig.SENTRY_DSN
            }
        }
    }

    override fun updateCrashReportingPreference(shouldLog: Boolean) {
        Sentry.init {
            it.dsn = if (shouldLog) BuildConfig.SENTRY_DSN else ""
        }
    }
}
