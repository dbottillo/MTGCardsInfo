package com.dbottillo.mtgsearchfree.ui.decks.deck

import com.dbottillo.mtgsearchfree.model.DeckCollection

interface DeckView {
    fun deckLoaded(title: String, collection: DeckCollection)
}