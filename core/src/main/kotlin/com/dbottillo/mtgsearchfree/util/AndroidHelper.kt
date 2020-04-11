package com.dbottillo.mtgsearchfree.util

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidHelper @Inject
constructor(
    private val context: Context
) {

    fun setNightMode(mode: String?) {
        val flag = when (mode) {
            "On" -> AppCompatDelegate.MODE_NIGHT_YES
            "Off" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                else
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            }
        }
        AppCompatDelegate.setDefaultNightMode(flag)
    }
}
