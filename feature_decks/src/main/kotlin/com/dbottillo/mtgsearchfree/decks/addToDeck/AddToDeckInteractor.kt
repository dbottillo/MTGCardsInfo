package com.dbottillo.mtgsearchfree.decks.addToDeck

import com.dbottillo.mtgsearchfree.interactor.SchedulerProvider
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.storage.CardsStorage
import com.dbottillo.mtgsearchfree.storage.DecksStorage
import com.dbottillo.mtgsearchfree.storage.GeneralData
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class AddToDeckInteractor @Inject constructor(
    private val decksStorage: DecksStorage,
    private val cardsStorage: CardsStorage,
    private val generalData: GeneralData,
    private val schedulerProvider: SchedulerProvider
) {
    fun init(cardId: Int, cardName: String): Single<AddToDeckData> {
        val decksSingle = Single.fromCallable { decksStorage.load() }
        val cardSingle = Single.fromCallable { cardsStorage.loadCard(cardId, cardName) }
        return Single.zip(
            decksSingle,
            cardSingle,
            BiFunction { decks: List<Deck>, card: MTGCard ->
                AddToDeckData(
                    decks,
                    generalData.lastDeckSelected,
                    card
                )
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