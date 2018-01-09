package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.model.Deck

interface DeckActivityView{
    fun deckExported()
    fun deckNotExported()
    fun deckCopied()
    fun showEmptyScreen()
    fun showTitle(title: String)
    fun showDeck(deck: Deck)
}