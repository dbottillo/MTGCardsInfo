package com.dbottillo.mtgsearchfree.ui.decks.startingHand

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlin.math.min

class StartingHandPresenter @Inject constructor(
    private val interactor: DecksInteractor,
    private val logger: Logger
) {

    lateinit var view: StartingHandView
    private var deckId: Long = 0
    var cards: MutableList<StartingHandCard>? = null

    private var disposable: CompositeDisposable = CompositeDisposable()

    fun init(view: StartingHandView, deckId: Long) {
        this.view = view
        this.deckId = deckId
    }

    fun loadDeck(fromBundle: Pair<List<StartingHandCard>?, List<StartingHandCard>?>) {
        val (cardsLeft, cardsShown) = fromBundle
        if (cardsLeft?.isNotEmpty() == true && cardsShown?.isNotEmpty() == true) {
            view.showOpeningHands(cardsShown.toMutableList())
            cards = cardsLeft.toMutableList()
        } else {
            loadDeck()
        }
    }

    private fun loadDeck() {
        disposable.add(interactor.loadDeck(deckId).subscribe({
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
        }, {
            logger.logNonFatal(it)
        }))
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