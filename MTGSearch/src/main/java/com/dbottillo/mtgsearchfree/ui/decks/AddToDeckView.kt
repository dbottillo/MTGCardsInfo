package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.model.Deck

interface AddToDeckView {
    fun decksLoaded(decks: List<Deck>)
    fun showError(message: String)
}