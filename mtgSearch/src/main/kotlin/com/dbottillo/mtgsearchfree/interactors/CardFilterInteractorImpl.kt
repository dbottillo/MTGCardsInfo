package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage
import com.dbottillo.mtgsearchfree.resources.CardFilter
import rx.Observable

class CardFilterInteractorImpl(var cardFilterStorage: CardFilterStorage) : CardFilterInteractor {

    override fun load(): Observable<CardFilter> {
        return Observable.just(cardFilterStorage.load());
    }

    override fun sync(filter: CardFilter) {
        cardFilterStorage.sync(filter)
    }


}
