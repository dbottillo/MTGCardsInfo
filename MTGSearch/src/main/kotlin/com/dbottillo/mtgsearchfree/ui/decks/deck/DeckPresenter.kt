package com.dbottillo.mtgsearchfree.ui.decks.deck

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class DeckPresenter @Inject constructor(
    private val interactor: DecksInteractor
) {

    lateinit var view: DeckView
    lateinit var deck: Deck
    private var disposable: CompositeDisposable = CompositeDisposable()

    fun init(view: DeckView, deckId: Long) {
        this.view = view
        disposable.add(interactor.loadDeckById(deckId)
                .toObservable()
                .concatMap { deck ->
                    interactor.loadDeck(deck.id).map {
                        Pair(deck, it)
                    }
                }
                .subscribe(this::deckLoaded, {}))
    }

    private fun deckLoaded(data: Pair<Deck, DeckCollection>) {
        this.deck = data.first
        view.deckLoaded("${deck.name} (${data.second.numberOfCardsWithoutSideboard()}/${data.second.numberOfCardsInSideboard()})", data.second)
    }

    fun addCardToDeck(card: MTGCard, quantity: Int) {
        disposable.add(interactor.addCard(deck, card, quantity).subscribe {
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})", it)
        })
    }

    fun removeCardFromDeck(card: MTGCard) {
        disposable.add(interactor.removeCard(deck, card).subscribe {
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})", it)
        })
    }

    fun removeAllCardFromDeck(card: MTGCard) {
        disposable.add(interactor.removeAllCard(deck, card).subscribe {
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})", it)
        })
    }

    fun moveCardFromSideBoard(card: MTGCard, quantity: Int) {
        disposable.add(interactor.moveCardFromSideboard(deck, card, quantity).subscribe {
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})", it)
        })
    }

    fun moveCardToSideBoard(card: MTGCard, quantity: Int) {
        disposable.add(interactor.moveCardToSideboard(deck, card, quantity).subscribe {
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})", it)
        })
    }

    fun onDestroyView() {
        disposable.dispose()
    }
}