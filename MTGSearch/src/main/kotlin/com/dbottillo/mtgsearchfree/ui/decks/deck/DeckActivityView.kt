package com.dbottillo.mtgsearchfree.ui.decks.deck

import android.net.Uri
import com.dbottillo.mtgsearchfree.model.Deck

interface DeckActivityView {
    fun deckExported(uri: Uri)
    fun deckNotExported()
    fun deckCopied()
    fun showEmptyScreen()
    fun showTitle(title: String)
    fun showDeck(deck: Deck)
}