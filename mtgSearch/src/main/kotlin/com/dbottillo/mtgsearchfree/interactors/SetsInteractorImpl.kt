package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.storage.SetsStorage
import com.dbottillo.mtgsearchfree.resources.MTGSet
import rx.Observable

class SetsInteractorImpl(var storage: SetsStorage) : SetsInteractor {

    override fun load(): Observable<List<MTGSet>> {
        return Observable.just(storage.load());
    }

}
