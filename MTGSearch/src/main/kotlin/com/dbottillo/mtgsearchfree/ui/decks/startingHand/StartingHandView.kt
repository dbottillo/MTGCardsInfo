package com.dbottillo.mtgsearchfree.ui.decks.startingHand

interface StartingHandView {
    fun showOpeningHands(cards: MutableList<StartingHandCard>)
    fun newCard(cards: StartingHandCard)
    fun clear()
}