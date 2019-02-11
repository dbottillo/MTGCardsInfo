package com.dbottillo.mtgsearchfree.ui.decks.startingHand

import android.os.Bundle
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.ui.decks.deck.DECK_KEY
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlin.math.min

class StartingHandPresenter @Inject constructor(private val interactor: DecksInteractor) {

    lateinit var view: StartingHandView
    lateinit var deck: Deck
    var cards: MutableList<StartingHandCard>? = null

    private var disposable: CompositeDisposable = CompositeDisposable()

    fun init(view: StartingHandView, arguments: Bundle?) {
        this.view = view
        deck = arguments?.get(DECK_KEY) as Deck
    }

    fun loadDeck(bundle: Bundle?) {
        bundle?.let {
            val array = bundle.getParcelableArrayList<StartingHandCard>(BUNDLE_KEY_LEFT)
            val shown = bundle.getParcelableArrayList<StartingHandCard>(BUNDLE_KEY_SHOWN)
            if (shown?.isNotEmpty() == true && array?.isNotEmpty() == true) {
                view.showOpeningHands(shown)
                cards = array
            } else {
                loadDeck()
            }
        } ?: loadDeck()
    }

    private fun loadDeck() {
        disposable.add(interactor.loadDeck(deck).subscribe {
            cards = mutableListOf()
            it.allCards()
                    .filter { !it.isSideboard }
                    .forEach { card ->
                        (1..card.quantity).forEach {
                            cards?.add(StartingHandCard(card.scryfallImage, card.name))
                        }
                    }
            cards?.shuffle()
            newStartingHand()
        })
    }

    fun repeat() {
        view.clear()
        loadDeck()
    }

    fun next() {
        if (cards?.isNotEmpty() == true) {
            cards?.removeAt(0)?.let { view.newCard(it) }
        }
    }

    private fun newStartingHand() {
        val initial: MutableList<StartingHandCard> = mutableListOf()
        cards?.let { startingHandCards ->
            (1..min(7, startingHandCards.size)).forEach { _ ->
                initial.add(startingHandCards.removeAt(0))
            }
        }
        view.showOpeningHands(initial)
    }

    fun onDestroyView() {
        disposable.clear()
    }
}