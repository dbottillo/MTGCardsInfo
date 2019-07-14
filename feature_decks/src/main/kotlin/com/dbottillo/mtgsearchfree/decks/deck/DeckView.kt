package com.dbottillo.mtgsearchfree.decks.deck

import com.dbottillo.mtgsearchfree.model.DeckCollection

interface DeckView {
    fun deckLoaded(title: String, collection: DeckCollection)
}