package com.dbottillo.mtgsearchfree.model.storage

class GeneralPreferencesForAndroidTest : GeneralData {

    private var debug = false
    private var typeList = false

    override fun setDebug() {
        debug = true
    }

    override fun isDebugEnabled(): Boolean {
        return debug
    }

    override fun setCardsShowTypeList() {
        typeList = true
    }

    override fun setCardsShowTypeGrid() {
        typeList = false
    }

    override val isCardsShowTypeGrid: Boolean
        get() = !typeList

    override fun setTooltipMainHide() {
    }

    override fun isTooltipMainToShow(): Boolean {
        return false
    }

    override fun getDefaultDuration(): Long {
        return 0
    }

    override fun isFreshInstall(): Boolean {
        return true
    }

    override fun cardMigrationRequired(): Boolean {
        return false
    }

    override fun markCardMigrationStarted() {
    }

    override var lastDeckSelected: Long = 2
}
