package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard

interface DeckActivityPresenter{
    fun init(view: DeckActivityView)
    fun loadDeck(deck: Deck)
    fun addCardToDeck(deck: Deck, card: MTGCard, quantity: Int)
    fun removeCardFromDeck(deck: Deck, card: MTGCard)
    fun removeAllCardFromDeck(deck: Deck, card: MTGCard)
    fun moveCardFromSideBoard(deck: Deck, card: MTGCard, quantity: Int)
    fun moveCardToSideBoard(deck: Deck, card: MTGCard, quantity: Int)
    fun exportDeck(deck: Deck, cards: CardsCollection)
    fun editDeck(deck: Deck, name: String)
}