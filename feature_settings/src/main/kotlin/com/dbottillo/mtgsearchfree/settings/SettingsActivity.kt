package com.dbottillo.mtgsearchfree.settings

import android.os.Bundle
import android.view.MenuItem
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import dagger.android.AndroidInjection

class SettingsActivity : BasicActivity() {

    override fun onCreate(bundle: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(bundle)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.preferences_container, SettingsFragment())
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else{
            false
        }
    }

    override fun getPageTrack() = "/settings"
}