package com.dbottillo.mtgsearchfree.decks.addToDeck

import android.os.Bundle
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AddToDeckPresenterImpl @Inject constructor(
    private val interactor: AddToDeckInteractor,
    private val logger: Logger
) : AddToDeckPresenter {

    lateinit var view: AddToDeckView
    lateinit var card: MTGCard
    private var disposable: CompositeDisposable = CompositeDisposable()

    init {
        logger.d("created")
    }

    override fun init(view: AddToDeckView, bundle: Bundle?) {
        logger.d()
        this.view = view

        val cardId = bundle?.getInt("card", -1) ?: -1
        val cardName = bundle?.getString("cardName", "") ?: ""

        disposable.add(interactor.init(cardId, cardName).subscribe({
            logger.d()
            this.card = it.card
            view.setCardTitle(card.name)
            view.decksLoaded(decks = it.decks, selectedDeck = it.selectedDeck)
        }, {
            if (it is MTGException) {
                view.showError(it.message ?: it.localizedMessage)
            } else {
                view.showError(it.localizedMessage ?: "")
            }
            logger.logNonFatal(it)
        }))
    }

    override fun addCardToDeck(deck: Deck, quantity: Int, side: Boolean) {
        logger.d("add $card to $deck")
        card.isSideboard = side
        interactor.addCard(deck, card, quantity)
    }

    override fun addCardToDeck(newDeck: String, quantity: Int, side: Boolean) {
        logger.d("add $card to $newDeck")
        card.isSideboard = side
        interactor.addCard(newDeck, card, quantity)
    }

    override fun onDestroyView() {
        disposable.clear()
    }
}

class AddToDeckData(val decks: List<Deck>, val selectedDeck: Long, val card: MTGCard)