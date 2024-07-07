package com.cmgcode.minimoods.handlers.error

import com.cmgcode.minimoods.BuildConfig
import com.cmgcode.minimoods.data.PreferenceDao
import com.cmgcode.minimoods.dependencies.CoroutineDispatchers
import io.sentry.Sentry
import io.sentry.SentryOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface ErrorHandler {
    fun init()
}

class SentryErrorHandler(
    private val prefs: PreferenceDao,
    dispatchers: CoroutineDispatchers
) : ErrorHandler {
    private val scope = CoroutineScope(dispatchers.io)

    override fun init() {
        scope.launch {
            prefs.shouldReportCrashes.collect { shouldReportCrashes ->
                enableSentry(shouldReportCrashes)
            }
        }
    }

    private fun enableSentry(shouldReportCrashes: Boolean?) {
        Sentry.init {
            it.dsn = BuildConfig.SENTRY_DSN
            it.isEnabled = shouldReportCrashes ?: false
            it.beforeSend = SentryOptions.BeforeSendCallback { event, _ -> event }
        }
    }
}
