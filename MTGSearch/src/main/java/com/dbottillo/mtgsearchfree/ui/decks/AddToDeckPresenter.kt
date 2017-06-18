package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard

interface AddToDeckPresenter {
    fun init(view: AddToDeckView)
    fun loadDecks()
    fun addCardToDeck(deck: Deck, card: MTGCard, quantity: Int)
    fun addCardToDeck(newDeck: String, card: MTGCard, quantity: Int)
}