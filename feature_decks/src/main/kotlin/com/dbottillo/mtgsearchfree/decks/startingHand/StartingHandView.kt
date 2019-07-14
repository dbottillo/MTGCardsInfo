package com.dbottillo.mtgsearchfree.decks.startingHand

interface StartingHandView {
    fun showOpeningHands(cards: MutableList<StartingHandCard>)
    fun newCard(cards: StartingHandCard)
    fun clear()
}