package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.util.Logger
import javax.inject.Inject

class DeckActivityPresenterImpl @Inject
constructor(private val interactor: DecksInteractor,
            runnerFactory: RunnerFactory,
            private val logger: Logger): DeckActivityPresenter {

    lateinit var view: DeckActivityView
    private val deckWrapper: Runner<DeckCollection> = runnerFactory.simple<DeckCollection>()
    private val exportWrapper: Runner<Boolean> = runnerFactory.simple<Boolean>()

    init {
        logger.d("created")
    }

    override fun init(view: DeckActivityView) {
        logger.d()
        this.view = view
    }

    override fun loadDeck(deck: Deck) {
        deckWrapper.run(interactor.loadDeck(deck), cardsObserver)
    }

    override fun addCardToDeck(deck: Deck, card: MTGCard, quantity: Int) {
        deckWrapper.run(interactor.addCard(deck, card, quantity), cardsObserver)
    }

    override fun removeCardFromDeck(deck: Deck, card: MTGCard) {
        deckWrapper.run(interactor.removeCard(deck, card), cardsObserver)
    }

    override fun removeAllCardFromDeck(deck: Deck, card: MTGCard) {
        deckWrapper.run(interactor.removeAllCard(deck, card), cardsObserver)
    }

    override fun moveCardFromSideBoard(deck: Deck, card: MTGCard, quantity: Int) {
        deckWrapper.run(interactor.moveCardFromSideboard(deck, card, quantity), cardsObserver)
    }

    override fun moveCardToSideBoard(deck: Deck, card: MTGCard, quantity: Int) {
        deckWrapper.run(interactor.moveCardToSideboard(deck, card, quantity), cardsObserver)
    }

    override fun exportDeck(deck: Deck, cards: CardsCollection) {
        exportWrapper.run(interactor.exportDeck(deck, cards), object : Runner.RxWrapperListener<Boolean> {
            override fun onNext(data: Boolean) {
                view.deckExported(data)
            }

            override fun onError(e: Throwable) {

            }

            override fun onCompleted() {

            }
        })
    }

    override fun editDeck(deck: Deck, name: String) {
        deckWrapper.run(interactor.editDeck(deck, name), cardsObserver)
    }

    private val cardsObserver = object : Runner.RxWrapperListener<DeckCollection> {
        override fun onNext(collection: DeckCollection) {
            logger.d()
            view.deckLoaded(collection)
        }

        override fun onError(e: Throwable) {

        }

        override fun onCompleted() {

        }
    }
}