package com.dbottillo.mtgsearchfree.settings

import android.os.Bundle
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import dagger.android.AndroidInjection

class SettingsActivity : BasicActivity() {

    override fun onCreate(bundle: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(bundle)
        setContentView(R.layout.activity_settings)

        setupToolbar(R.id.toolbar)
        title = getString(R.string.action_settings)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.preferences_container, SettingsFragment())
                .commit()
    }

    override fun getPageTrack() = "/settings"
}