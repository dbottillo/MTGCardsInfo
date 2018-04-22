package com.dbottillo.mtgsearchfree.ui.decks.deck

import android.os.Bundle
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class DeckPresenter @Inject constructor(private val interactor: DecksInteractor,
                                        private val logger: Logger) {

    lateinit var view: DeckView
    lateinit var deck: Deck
    private var disposable: CompositeDisposable = CompositeDisposable()

    fun init(view: DeckView, arguments: Bundle?) {
        this.view = view
        deck = arguments?.get(DECK_KEY) as Deck
    }

    fun loadDeck() {
        disposable.add(interactor.loadDeck(deck).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun addCardToDeck(card: MTGCard, quantity: Int) {
        disposable.add(interactor.addCard(deck, card, quantity).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun removeCardFromDeck(card: MTGCard) {
        disposable.add(interactor.removeCard(deck, card).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun removeAllCardFromDeck(card: MTGCard) {
        disposable.add(interactor.removeAllCard(deck, card).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun moveCardFromSideBoard(card: MTGCard, quantity: Int) {
        disposable.add(interactor.moveCardFromSideboard(deck, card, quantity).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun moveCardToSideBoard(card: MTGCard, quantity: Int) {
        disposable.add(interactor.moveCardToSideboard(deck, card, quantity).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun onDestroyView(){
        disposable.dispose()
    }

}