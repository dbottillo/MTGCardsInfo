package com.dbottillo.mtgsearchfree

interface AppPreferences {
    fun shouldShowNewUpdateBanner(): Boolean
    fun clear()
}