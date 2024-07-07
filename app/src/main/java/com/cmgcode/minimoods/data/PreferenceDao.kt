package com.cmgcode.minimoods.data

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.preference.PreferenceManager
import com.cmgcode.minimoods.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface PreferenceDao {
    val shouldReportCrashes: Flow<Boolean?>

    suspend fun updateCrashReporting(value: Boolean?)
}


class PreferenceDaoImpl @Inject constructor(@ApplicationContext context: Context) : PreferenceDao {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val loggingKey = context.getString(R.string.key_should_log_crashes)

    override val shouldReportCrashes: Flow<Boolean?> = callbackFlow {
        fun emitLoggingKey() {
            val newValue = if (!preferences.contains(loggingKey)) {
                null
            } else {
                preferences.getBoolean(loggingKey, false)
            }

            trySend(newValue)
        }

        val listener = OnSharedPreferenceChangeListener { _, key ->
            if (key == loggingKey) {
                emitLoggingKey()
            }
        }

        preferences.registerOnSharedPreferenceChangeListener(listener)

        emitLoggingKey()

        awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun updateCrashReporting(value: Boolean?) {
        preferences
            .edit()
            .run {
                if (value == null) {
                    remove(loggingKey)
                } else {
                    putBoolean(loggingKey, value)
                }
            }
            .apply()
    }
}
