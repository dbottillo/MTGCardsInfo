package com.dbottillo.mtgsearchfree.ui.decks

import android.os.Bundle
import com.dbottillo.mtgsearchfree.model.Deck

interface AddToDeckPresenter {
    fun init(view: AddToDeckView, bundle: Bundle?)
    fun addCardToDeck(deck: Deck, quantity: Int, side: Boolean)
    fun addCardToDeck(newDeck: String, quantity: Int, side: Boolean)
    fun onDestroyView()
}