package com.dbottillo.mtgsearchfree.model.storage

import android.content.Context
import android.content.SharedPreferences

import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.util.AppInfo

class GeneralPreferences(context: Context, private val appInfo: AppInfo) : GeneralData {

    private val sharedPreferences: SharedPreferences by lazy(LazyThreadSafetyMode.NONE) { context.getSharedPreferences("General", Context.MODE_PRIVATE) }

    override fun setDebug() {
        sharedPreferences.edit().putBoolean(DEBUG, true).apply()
    }

    override fun isDebugEnabled(): Boolean {
        return BuildConfig.DEBUG || sharedPreferences.getBoolean(DEBUG, false)
    }

    override fun setCardsShowTypeList() {
        sharedPreferences.edit().putString(CARDS_SHOW_TYPE, "List").apply()
    }

    override fun setCardsShowTypeGrid() {
        sharedPreferences.edit().putString(CARDS_SHOW_TYPE, "Grid").apply()
    }

    override val isCardsShowTypeGrid: Boolean
        get() = sharedPreferences.getString(CARDS_SHOW_TYPE, "Grid")!!.equals("Grid", ignoreCase = true)

    override var lastDeckSelected: Long
        get() = sharedPreferences.getLong(LAST_DECK_SELECTED, -1)
        set(value) = sharedPreferences.edit().putLong(LAST_DECK_SELECTED, value).apply()

    override fun setTooltipMainHide() {
        sharedPreferences.edit().putBoolean(TOOLTIP_MAIN_SHOWN, false).apply()
    }

    override fun isTooltipMainToShow(): Boolean {
        return !isFreshInstall() && sharedPreferences.getBoolean(TOOLTIP_MAIN_SHOWN, true)
    }

    override fun getDefaultDuration(): Long {
        return 200
    }

    override fun isFreshInstall(): Boolean {
        val firstInstallTime = appInfo.firstInstallTime
        val lastUpdateTime = appInfo.lastUpdateTime
        return firstInstallTime == lastUpdateTime
    }

    override fun cardMigrationRequired(): Boolean {
        return false
    }

    override fun markCardMigrationStarted() {
        sharedPreferences.edit().putBoolean(CARD_MIGRATION_REQUIRED, false).apply()
    }
}

const val DEBUG = "debug"
const val CARDS_SHOW_TYPE = "cardShowType"
const val TOOLTIP_MAIN_SHOWN = "tooltipMainShow2"
const val CARD_MIGRATION_REQUIRED = "cardMigrationRequired"
const val LAST_DECK_SELECTED = "lastDeckSelected"

interface GeneralData {
    fun isDebugEnabled(): Boolean
    val isCardsShowTypeGrid: Boolean
    fun isTooltipMainToShow(): Boolean
    fun getDefaultDuration(): Long
    fun isFreshInstall(): Boolean
    fun setDebug()
    fun setCardsShowTypeList()
    fun setCardsShowTypeGrid()
    fun setTooltipMainHide()
    fun cardMigrationRequired(): Boolean
    fun markCardMigrationStarted()
    var lastDeckSelected: Long
}
