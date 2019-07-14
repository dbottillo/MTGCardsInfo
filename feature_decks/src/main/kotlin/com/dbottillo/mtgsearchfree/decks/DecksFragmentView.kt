package com.dbottillo.mtgsearchfree.decks

import com.dbottillo.mtgsearchfree.model.Deck

interface DecksFragmentView {
    fun decksLoaded(decks: List<Deck>)
    fun showError(message: String?)
}