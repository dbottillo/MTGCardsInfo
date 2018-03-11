package com.dbottillo.mtgsearchfree.ui.decks.deck

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.util.Logger
import javax.inject.Inject

class DeckActivityPresenter @Inject constructor(
        private val interactor: DecksInteractor,
        private val logger: Logger) {

    lateinit var view: DeckActivityView
    lateinit var deck: Deck

    init {
        logger.d("created")
    }

    fun init(view: DeckActivityView, deck: Deck) {
        logger.d()
        this.view = view
        this.deck = deck
    }

    fun load(){
        if (deck.numberOfCards == 0){
            view.showEmptyScreen()
            view.showTitle(deck.name)
        } else {
            view.showDeck(deck)
            deckLoaded()
        }
    }

    fun editDeck(name: String) {
        interactor.editDeck(deck, name).subscribe({
            this.deck = it
            deckLoaded()
        }, {})
    }

    fun exportDeck() {
        interactor.exportDeck(deck).subscribe({
            view.deckExported()
        }, {
            view.deckNotExported()
        })
    }

    fun copyDeck() {
        interactor.copy(deck).subscribe({
            view.deckCopied()
        }, {})
    }

    private fun deckLoaded(){
        view.showTitle("${deck.name} (${deck.numberOfCards-deck.sizeOfSideboard}/${deck.sizeOfSideboard})")
    }

}