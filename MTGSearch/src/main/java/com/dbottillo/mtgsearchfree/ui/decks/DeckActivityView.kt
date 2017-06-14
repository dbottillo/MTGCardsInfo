package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.model.DeckCollection

interface DeckActivityView{
    fun deckLoaded(deckCollection: DeckCollection)
    fun deckExported(success: Boolean)
}