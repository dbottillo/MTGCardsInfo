package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import javax.inject.Inject

class AddToDeckPresenterImpl @Inject constructor(private val interactor: DecksInteractor,
                                                 private val logger: Logger) : AddToDeckPresenter {

    lateinit var view: AddToDeckView

    init {
        logger.d("created")
    }

    override fun init(view: AddToDeckView) {
        this.view = view
    }

    override fun loadDecks() {
        logger.d()
        interactor.load().subscribe({
            logger.d()
            view.decksLoaded(it)
        }, {
            showError(it)
        })
    }

    override fun addCardToDeck(deck: Deck, card: MTGCard, quantity: Int) {
        logger.d("add $card to $deck")
        interactor.addCard(deck, card, quantity)
    }

    override fun addCardToDeck(newDeck: String, card: MTGCard, quantity: Int) {
        logger.d("add $card to $newDeck")
        interactor.addCard(newDeck, card, quantity)
    }

    internal fun showError(e: Throwable) {
        if (e is MTGException) {
            view.showError(e.message ?: e.localizedMessage)
        } else {
            view.showError(e.localizedMessage)
        }
    }

}