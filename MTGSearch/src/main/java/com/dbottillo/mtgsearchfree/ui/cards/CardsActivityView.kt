package com.dbottillo.mtgsearchfree.ui.cards

import com.dbottillo.mtgsearchfree.model.CardsCollection

interface CardsActivityView {
    fun finish()
    fun updateTitle(name: String)
    fun updateTitle(resource: Int)
    fun updateAdapter(cards: CardsCollection, isDeck: Boolean, showImage: Boolean, startPosition: Int)
    fun showFavMenuItem()
    fun updateFavMenuItem(text: Int, icon: Int)
    fun hideFavMenuItem()
    fun setImageMenuItemChecked(checked: Boolean)
}