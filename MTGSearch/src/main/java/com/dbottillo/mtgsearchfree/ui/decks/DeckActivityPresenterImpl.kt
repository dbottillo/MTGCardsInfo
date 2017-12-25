package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import javax.inject.Inject

class DeckActivityPresenterImpl @Inject constructor(
        private val interactor: DecksInteractor,
        private val logger: Logger) : DeckActivityPresenter {

    lateinit var view: DeckActivityView

    init {
        logger.d("created")
    }

    override fun init(view: DeckActivityView) {
        logger.d()
        this.view = view
    }

    override fun loadDeck(deck: Deck) {
        interactor.loadDeck(deck).subscribe({
            view.deckLoaded(it)
        })
    }

    override fun addCardToDeck(deck: Deck, card: MTGCard, quantity: Int) {
        interactor.addCard(deck, card, quantity).subscribe({
            view.deckLoaded(it)
        })
    }

    override fun removeCardFromDeck(deck: Deck, card: MTGCard) {
        interactor.removeCard(deck, card).subscribe({
            view.deckLoaded(it)
        })
    }

    override fun removeAllCardFromDeck(deck: Deck, card: MTGCard) {
        interactor.removeAllCard(deck, card).subscribe({
            view.deckLoaded(it)
        })
    }

    override fun moveCardFromSideBoard(deck: Deck, card: MTGCard, quantity: Int) {
        interactor.moveCardFromSideboard(deck, card, quantity).subscribe({
            view.deckLoaded(it)
        })
    }

    override fun moveCardToSideBoard(deck: Deck, card: MTGCard, quantity: Int) {
        interactor.moveCardToSideboard(deck, card, quantity).subscribe({
            view.deckLoaded(it)
        })
    }

    override fun editDeck(deck: Deck, name: String) {
        interactor.editDeck(deck, name).subscribe({
            view.deckLoaded(it)
        })
    }

    override fun exportDeck(deck: Deck, cards: CardsCollection) {
        interactor.exportDeck(deck, cards).subscribe({
            view.deckExported(it)
        })
    }

    override fun copyDeck(deck: Deck) {
        interactor.copy(deck).subscribe({
            view.deckCopied()
        }, {})
    }

}