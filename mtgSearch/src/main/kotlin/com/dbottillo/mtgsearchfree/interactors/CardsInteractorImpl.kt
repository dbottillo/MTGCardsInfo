package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*

class CardsInteractorImpl(var storage: CardsStorage) : CardsInteractor {

    override fun saveAsFavourite(card: MTGCard) {
        Observable.just(storage.saveAsFavourite(card)).subscribeOn(Schedulers.io()).subscribe()
    }

    override fun load(set: MTGSet): Observable<ArrayList<MTGCard>> {
        return Observable.just(storage.load(set));
    }

}
