package com.dbottillo.mtgsearchfree

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.telephony.TelephonyManager
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.dbottillo.mtgsearchfree.dagger.DaggerAppComponent
import com.dbottillo.mtgsearchfree.dagger.DataModule
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.AndroidHelper
import com.dbottillo.mtgsearchfree.util.LOG
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.util.Locale
import javax.inject.Inject

open class MTGApp : Application(), HasAndroidInjector {

    @Inject lateinit var cardsPreferences: CardsPreferences
    @Inject lateinit var androidHelper: AndroidHelper
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        LOG.d("============================================")
        LOG.d("            MTGApp created")
        LOG.d("============================================")

        initDagger()

        if (!isTesting()) {
            if (BuildConfig.DEBUG) {
                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build())
                StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .build())
            }
        }
        checkDefaultPriceProviderPreference()
    }

    private fun checkDefaultPriceProviderPreference() {
        val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (defaultPrefs.getString(PRICE_PROVIDER_PREFERENCE_KEY, null) == null) {
            defaultPrefs.edit().putString(PRICE_PROVIDER_PREFERENCE_KEY, if (isEuUser()) "MKM" else "TCG").apply()
        }

        if (defaultPrefs.getString(NIGHT_MODE_PREFERENCE_KEY, null) == null) {
            defaultPrefs.edit().putString(NIGHT_MODE_PREFERENCE_KEY, "Auto").apply()
        }
        androidHelper.setNightMode(defaultPrefs.getString(NIGHT_MODE_PREFERENCE_KEY, null))
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    protected open fun isTesting(): Boolean {
        return false
    }

    protected open fun generateDataModule(): DataModule {
        return DataModule()
    }

    private fun initDagger() {
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)
    }
}

private fun Context.isEuUser(): Boolean {
    val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return listOf(
            "BE", "EL", "LT", "PT", "BG", "ES", "LU", "RO", "CZ", "FR", "HU", "SI", "DK", "HR",
            "MT", "SK", "DE", "IT", "NL", "FI", "EE", "CY", "AT", "SE", "IE", "LV", "PL", "UK",
            "CH", "NO", "IS", "LI", "GB"
    ).contains((tm.simCountryIso ?: Locale.getDefault().country).uppercase())
}

private const val PRICE_PROVIDER_PREFERENCE_KEY = "price_provider"
private const val NIGHT_MODE_PREFERENCE_KEY = "night_mode"