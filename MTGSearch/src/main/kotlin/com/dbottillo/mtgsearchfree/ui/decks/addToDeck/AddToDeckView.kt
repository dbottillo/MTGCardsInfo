package com.dbottillo.mtgsearchfree.ui.decks.addToDeck

import com.dbottillo.mtgsearchfree.model.Deck

interface AddToDeckView {
    fun decksLoaded(decks: List<Deck>)
    fun showError(message: String)
    fun setCardTitle(cardName: String)
}