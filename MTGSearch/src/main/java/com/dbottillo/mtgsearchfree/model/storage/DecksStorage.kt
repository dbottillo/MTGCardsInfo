package com.dbottillo.mtgsearchfree.model.storage

import android.net.Uri
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard

interface DecksStorage {

    fun load(): List<Deck>
    fun addDeck(name: String): List<Deck>
    fun deleteDeck(deck: Deck): List<Deck>
    fun loadDeck(deck: Deck): CardsCollection
    fun editDeck(deck: Deck, name: String): CardsCollection
    fun addCard(deck: Deck, card: MTGCard, quantity: Int): CardsCollection
    fun addCard(name: String, card: MTGCard, quantity: Int): CardsCollection
    fun removeCard(deck: Deck, card: MTGCard): CardsCollection
    fun removeAllCard(deck: Deck, card: MTGCard): CardsCollection
    @Throws(MTGException::class) fun importDeck(uri: Uri): List<Deck>
    fun moveCardFromSideboard(deck: Deck, card: MTGCard, quantity: Int): CardsCollection
    fun moveCardToSideboard(deck: Deck, card: MTGCard, quantity: Int): CardsCollection
}

