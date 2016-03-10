package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import rx.Observable

class CardsInteractorImpl(var storage: CardsStorage) : CardsInteractor {
    override fun load(set: MTGSet): Observable<List<MTGCard>> {
        return Observable.just(storage.load(set));
    }

}
