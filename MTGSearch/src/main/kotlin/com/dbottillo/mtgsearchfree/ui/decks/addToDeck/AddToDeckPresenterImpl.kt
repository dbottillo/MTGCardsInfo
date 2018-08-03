package com.dbottillo.mtgsearchfree.ui.decks.addToDeck

import android.os.Bundle
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.SchedulerProvider
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class AddToDeckPresenterImpl @Inject constructor(private val interactor: AddToDeckInteractor,
                                                 private val logger: Logger) : AddToDeckPresenter {

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

        disposable.add(interactor.init(cardId).subscribe({
            logger.d()
            this.card = it.card
            view.setCardTitle(card.name)
            view.decksLoaded(decks = it.decks, selectedDeck = it.selectedDeck)
        }, {
            if (it is MTGException) {
                view.showError(it.message ?: it.localizedMessage)
            } else {
                view.showError(it.localizedMessage)
            }
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

class AddToDeckInteractor @Inject constructor(private val decksStorage: DecksStorage,
                                              private val cardsStorage: CardsStorage,
                                              private val generalData: GeneralData,
                                              private val schedulerProvider: SchedulerProvider) {
    fun init(cardId: Int): Single<AddToDeckData> {
        val decksSingle = Single.fromCallable { decksStorage.load() }
        val cardSingle = Single.fromCallable { cardsStorage.loadCard(cardId) }
        return Single.zip(decksSingle, cardSingle, BiFunction { decks: List<Deck>, card: MTGCard ->
            AddToDeckData(decks, generalData.lastDeckSelected, card)
        }).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
    }

    fun addCard(name: String, card: MTGCard, quantity: Int) {
        Completable.fromCallable { decksStorage.addCard(name, card, quantity) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui()).subscribe()
    }

    fun addCard(deck: Deck, card: MTGCard, quantity: Int) {
        Completable.fromCallable { decksStorage.addCard(deck, card, quantity) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui()).subscribe()
    }

}

class AddToDeckData(val decks: List<Deck>, val selectedDeck: Long, val card: MTGCard)