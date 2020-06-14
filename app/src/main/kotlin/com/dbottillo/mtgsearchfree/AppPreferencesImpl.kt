package com.dbottillo.mtgsearchfree

import android.content.Context
import android.content.SharedPreferences
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.util.TrackingManagerImpl
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
    private val context: Context,
    private val cardsPreferences: CardsPreferences,
    private val trackingManager: TrackingManager
) : AppPreferences {

    private val sharedPreferences: SharedPreferences by lazy(LazyThreadSafetyMode.NONE) { context.getSharedPreferences(PREFS_NAME, 0) }

    override fun shouldShowNewUpdateBanner(): Boolean {
        return if (versionCode < BuildConfig.VERSION_CODE) {
            trackingManager.trackReleaseNote()
            cardsPreferences.saveSetPosition(0)
            sharedPreferences.edit().putInt("VersionCode", BuildConfig.VERSION_CODE).apply()
            true
        } else false
    }

    override fun clear() {
        sharedPreferences.edit().putInt("VersionCode", -1).apply()
    }

    private val versionCode: Int
        get() = sharedPreferences.getInt("VersionCode", -1)
}

private const val PREFS_NAME = "com.dbottillo.mtgsearchfree.app_preferences_impl"