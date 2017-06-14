package com.dbottillo.mtgsearchfree.model.storage

import android.net.Uri
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard

interface DecksStorage {

    fun load(): List<Deck>
    fun addDeck(name: String): List<Deck>
    fun deleteDeck(deck: Deck): List<Deck>
    fun loadDeck(deck: Deck): DeckCollection
    fun editDeck(deck: Deck, name: String): DeckCollection
    fun addCard(deck: Deck, card: MTGCard, quantity: Int): DeckCollection
    fun addCard(name: String, card: MTGCard, quantity: Int): DeckCollection
    fun removeCard(deck: Deck, card: MTGCard): DeckCollection
    fun removeAllCard(deck: Deck, card: MTGCard): DeckCollection
    @Throws(MTGException::class) fun importDeck(uri: Uri): List<Deck>
    fun moveCardFromSideboard(deck: Deck, card: MTGCard, quantity: Int): DeckCollection
    fun moveCardToSideboard(deck: Deck, card: MTGCard, quantity: Int): DeckCollection
}

