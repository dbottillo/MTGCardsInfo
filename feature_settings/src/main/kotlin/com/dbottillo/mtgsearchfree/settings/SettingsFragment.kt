package com.dbottillo.mtgsearchfree.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.dbottillo.mtgsearchfree.util.AndroidHelper
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject lateinit var androidHelper: AndroidHelper

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val nightModePref = preferenceManager.findPreference<DropDownPreference>("night_mode")

        nightModePref?.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue ->
            androidHelper.setNightMode(newValue as String)
            true
        }
    }

}