package com.dbottillo.mtgsearchfree.presenter

import android.net.Uri

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.view.DecksView

interface DecksPresenter {

    fun init(view: DecksView)

    fun loadDecks()

    fun loadDeck(deck: Deck)

    fun addDeck(name: String)

    fun deleteDeck(deck: Deck)

    fun editDeck(deck: Deck, name: String)

    fun addCardToDeck(deck: Deck, card: MTGCard, quantity: Int)

    fun addCardToDeck(name: String, card: MTGCard, quantity: Int)

    fun removeCardFromDeck(deck: Deck, card: MTGCard)

    fun removeAllCardFromDeck(deck: Deck, card: MTGCard)

    fun moveCardFromSideBoard(deck: Deck, card: MTGCard, quantity: Int)

    fun moveCardToSideBoard(deck: Deck, card: MTGCard, quantity: Int)

    fun importDeck(uri: Uri)

    fun exportDeck(deck: Deck, cards: CardsCollection)
}