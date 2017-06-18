package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.util.Logger
import javax.inject.Inject

class AddToDeckPresenterImpl @Inject constructor(private val interactor: DecksInteractor,
                                                runnerFactory: RunnerFactory,
                                                private val logger: Logger) : AddToDeckPresenter{

    lateinit var view: AddToDeckView

    private val decksWrapper: Runner<List<Deck>> = runnerFactory.simple<List<Deck>>()

    private val deckWrapper: Runner<DeckCollection> = runnerFactory.simple<DeckCollection>()

    init {
        logger.d("created")
    }

    override fun init(view: AddToDeckView) {
        this.view = view
    }

    override fun loadDecks() {
        logger.d()
        decksWrapper.run(interactor.load(), decksObserver)
    }

    override fun addCardToDeck(deck: Deck, card: MTGCard, quantity: Int) {
        logger.d("add $deck")
        deckWrapper.run(interactor.addCard(deck, card, quantity), null)
    }

    override fun addCardToDeck(newDeck: String, card: MTGCard, quantity: Int) {
        logger.d("add $newDeck")
        deckWrapper.run(interactor.addCard(newDeck, card, quantity), null)
    }

    private val decksObserver = object : Runner.RxWrapperListener<List<Deck>> {
        override fun onNext(decks: List<Deck>) {
            logger.d()
            view.decksLoaded(decks)
        }

        override fun onError(e: Throwable) {
            if (e is MTGException) {
                view.showError(e.message ?: "")
            } else {
                view.showError(e.localizedMessage)
            }
        }

        override fun onCompleted() {

        }
    }

}