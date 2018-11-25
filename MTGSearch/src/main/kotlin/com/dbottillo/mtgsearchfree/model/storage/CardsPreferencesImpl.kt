package com.dbottillo.mtgsearchfree.model.storage

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.VisibleForTesting

import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.Rarity
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import com.dbottillo.mtgsearchfree.util.LOG

open class CardsPreferencesImpl(context: Context) : CardsPreferences {

    private val sharedPreferences: SharedPreferences by lazy(LazyThreadSafetyMode.NONE) { context.getSharedPreferences(PREFS_NAME, 0) }

    override fun load(): CardFilter {
        LOG.d("")
        return CardFilter().apply {
            white = sharedPreferences.getBoolean(WHITE, true)
            blue = sharedPreferences.getBoolean(BLUE, true)
            black = sharedPreferences.getBoolean(BLACK, true)
            red = sharedPreferences.getBoolean(RED, true)
            green = sharedPreferences.getBoolean(GREEN, true)

            artifact = sharedPreferences.getBoolean(ARTIFACT, true)
            land = sharedPreferences.getBoolean(LAND, true)
            eldrazi = sharedPreferences.getBoolean(ELDRAZI, true)

            common = sharedPreferences.getBoolean(Rarity.COMMON.value, true)
            uncommon = sharedPreferences.getBoolean(Rarity.UNCOMMON.value, true)
            rare = sharedPreferences.getBoolean(Rarity.RARE.value, true)
            mythic = sharedPreferences.getBoolean(Rarity.MYTHIC.value, true)

            sortWUBGR = sharedPreferences.getBoolean(BasicFragment.PREF_SORT_WUBRG, true)
        }
    }

    override fun sync(filter: CardFilter) {
        LOG.d("")
        sharedPreferences.edit()
                .putBoolean(WHITE, filter.white)
                .putBoolean(BLUE, filter.blue)
                .putBoolean(BLACK, filter.black)
                .putBoolean(RED, filter.red)
                .putBoolean(GREEN, filter.green)
                .putBoolean(ARTIFACT, filter.artifact)
                .putBoolean(LAND, filter.land)
                .putBoolean(ELDRAZI, filter.eldrazi)
                .putBoolean(Rarity.COMMON.value, filter.common)
                .putBoolean(Rarity.UNCOMMON.value, filter.uncommon)
                .putBoolean(Rarity.RARE.value, filter.rare)
                .putBoolean(Rarity.MYTHIC.value, filter.mythic)
                .putBoolean(BasicFragment.PREF_SORT_WUBRG, filter.sortWUBGR)
                .apply()
    }

    override fun saveSetPosition(position: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("setPosition", position)
        editor.apply()
    }

    override val setPosition: Int
        get() = sharedPreferences.getInt("setPosition", 0)

    override fun showPoison(): Boolean {
        return sharedPreferences.getBoolean("poison", false)
    }

    override fun twoHGEnabled(): Boolean {
        return sharedPreferences.getBoolean(BasicFragment.PREF_TWO_HG_ENABLED, false)
    }

    override fun screenOn(): Boolean {
        return sharedPreferences.getBoolean(BasicFragment.PREF_SCREEN_ON, false)
    }

    override fun showPoison(show: Boolean) {
        sharedPreferences.edit().putBoolean("poison", show).apply()
    }

    override fun setScreenOn(on: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(BasicFragment.PREF_SCREEN_ON, on)
        editor.apply()
    }

    override fun setTwoHGEnabled(enabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(BasicFragment.PREF_TWO_HG_ENABLED, enabled)
        editor.apply()
    }

    override fun showImage(): Boolean {
        return sharedPreferences.getBoolean(BasicFragment.PREF_SHOW_IMAGE, true)
    }

    override fun setShowImage(show: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(BasicFragment.PREF_SHOW_IMAGE, show)
        editor.apply()
    }

    override val versionCode: Int
        get() = sharedPreferences.getInt("VersionCode", -1)

    override fun saveVersionCode() {
        sharedPreferences.edit().putInt("VersionCode", BuildConfig.VERSION_CODE).apply()
    }

    @VisibleForTesting
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}

private const val PREFS_NAME = "Filter"
private const val WHITE = "White"
private const val BLUE = "Blue"
private const val BLACK = "Black"
private const val RED = "Red"
private const val GREEN = "Green"
private const val ARTIFACT = "Artifact"
private const val LAND = "Land"
private const val ELDRAZI = "Eldrazi"

interface CardsPreferences {
    val versionCode: Int
    fun load(): CardFilter
    fun sync(filter: CardFilter)
    fun saveSetPosition(position: Int)
    val setPosition: Int
    fun showPoison(): Boolean
    fun twoHGEnabled(): Boolean
    fun screenOn(): Boolean
    fun showPoison(show: Boolean)
    fun setScreenOn(on: Boolean)
    fun setTwoHGEnabled(enabled: Boolean)
    fun showImage(): Boolean
    fun setShowImage(show: Boolean)
    fun saveVersionCode()
}
