package com.dbottillo.mtgsearchfree.ui.decks.startingHand

import android.os.Bundle
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.ui.decks.deck.DECK_KEY
import javax.inject.Inject
import kotlin.math.min

class StartingHandPresenter @Inject constructor(private val interactor: DecksInteractor) {

    lateinit var view: StartingHandView
    lateinit var deck: Deck
    lateinit var cards: MutableList<StartingHandCard>

    fun init(view: StartingHandView, arguments: Bundle?) {
        this.view = view
        deck = arguments?.get(DECK_KEY) as Deck
    }

    fun loadDeck(bundle: Bundle?) {
        bundle?.let {
            val array = bundle.getParcelableArrayList<StartingHandCard>(BUNDLE_KEY_LEFT)
            val shown = bundle.getParcelableArrayList<StartingHandCard>(BUNDLE_KEY_SHOWN)
            if (shown.isNotEmpty() && array.isNotEmpty()) {
                view.showOpeningHands(shown)
                cards = array
            } else {
                loadDeck()
            }
        } ?: loadDeck()
    }

    private fun loadDeck() {
        interactor.loadDeck(deck).subscribe({
            cards = mutableListOf()
            it.allCards()
                    .filter { !it.isSideboard }
                    .forEach { card ->
                        (1..card.quantity).forEach {
                            cards.add(StartingHandCard(card.mtgCardsInfoImage, card.gathererImage, card.name))
                        }
                    }
            cards.shuffle()
            newStartingHand()
        })
    }

    fun repeat() {
        view.clear()
        loadDeck()
    }

    fun next() {
        if (cards.isNotEmpty()) {
            view.newCard(cards.removeAt(0))
        }
    }

    private fun newStartingHand() {
        val initial: MutableList<StartingHandCard> = mutableListOf()
        (1..min(7, cards.size)).forEach {
            initial.add(cards.removeAt(0))
        }
        view.showOpeningHands(initial)
    }

}