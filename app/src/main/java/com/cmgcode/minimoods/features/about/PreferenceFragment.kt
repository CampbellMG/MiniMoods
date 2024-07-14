package com.cmgcode.minimoods.features.about

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.cmgcode.minimoods.R

class PreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) =
        setPreferencesFromResource(R.xml.preferences, rootKey)
}
