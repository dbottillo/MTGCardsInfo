package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.Logger

import io.reactivex.Observable

class CardFilterInteractorImpl(
    private val cardsPreferences: CardsPreferences,
    private val logger: Logger
) : CardFilterInteractor {

    init {
        logger.d("created")
    }

    override fun load(): Observable<CardFilter> {
        logger.d("loadSet")
        return Observable.just(cardsPreferences.load())
    }

    override fun sync(filter: CardFilter) {
        logger.d("sync")
        cardsPreferences.sync(filter)
    }
}
