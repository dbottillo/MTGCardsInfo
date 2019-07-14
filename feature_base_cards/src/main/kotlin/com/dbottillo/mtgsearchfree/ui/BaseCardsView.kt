package com.dbottillo.mtgsearchfree.ui

interface BaseCardsView {
    fun showFavMenuItem()
    fun updateFavMenuItem(text: Int, icon: Int)
    fun hideFavMenuItem()
    fun setImageMenuItemChecked(checked: Boolean)
}