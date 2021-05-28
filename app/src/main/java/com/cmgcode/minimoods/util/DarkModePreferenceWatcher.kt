package com.cmgcode.minimoods.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.cmgcode.minimoods.R

class DarkModePreferenceWatcher(context: Context) : SharedPreferences.OnSharedPreferenceChangeListener {

    private val key = context.getString(R.string.key_theme)
    private val options = context.resources.getStringArray(R.array.theme_values)

    init {
        updateNightMode(getDefaultSharedPreferences(context))
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key !== this.key) {
            return
        }

        updateNightMode(sharedPreferences)
    }

    private fun updateNightMode(sharedPreferences: SharedPreferences?) {
        val preference = sharedPreferences?.getString(key, null)

        setDefaultNightMode(mapPreferenceToNightMode(preference))
    }

    private fun mapPreferenceToNightMode(preference: String?) = when (preference) {
        options[2] -> MODE_NIGHT_YES
        options[1] -> MODE_NIGHT_NO
        else -> MODE_NIGHT_FOLLOW_SYSTEM
    }
}
