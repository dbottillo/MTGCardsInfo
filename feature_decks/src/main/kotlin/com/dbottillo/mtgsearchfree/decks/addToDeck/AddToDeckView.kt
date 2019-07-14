package com.dbottillo.mtgsearchfree.decks.addToDeck

import com.dbottillo.mtgsearchfree.model.Deck

interface AddToDeckView {
    fun decksLoaded(decks: List<Deck>, selectedDeck: Long)
    fun showError(message: String)
    fun setCardTitle(cardName: String)
}