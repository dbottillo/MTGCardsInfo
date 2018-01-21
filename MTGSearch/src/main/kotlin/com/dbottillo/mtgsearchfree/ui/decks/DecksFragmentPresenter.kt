package com.dbottillo.mtgsearchfree.ui.decks

import android.net.Uri
import com.dbottillo.mtgsearchfree.model.Deck

interface DecksFragmentPresenter{
    fun init(view: DecksFragmentView)
    fun loadDecks()
    fun addDeck(name: String)
    fun deleteDeck(deck: Deck)
    fun importDeck(uri: Uri)
    fun copyDeck(deck: Deck)
}