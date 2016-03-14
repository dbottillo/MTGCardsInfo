package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*

class CardsInteractorImpl(var storage: CardsStorage) : CardsInteractor {
    override fun getLuckyCards(howMany: Int): Observable<ArrayList<MTGCard>> {
        return Observable.just(storage.getLuckyCards(howMany));
    }

    override fun saveAsFavourite(card: MTGCard): Observable<IntArray> {
        return Observable.just(storage.saveAsFavourite(card));
    }

    override fun removeFromFavourite(card: MTGCard): Observable<IntArray> {
        return Observable.just(storage.removeFromFavourite(card));
    }

    override fun load(set: MTGSet): Observable<ArrayList<MTGCard>> {
        return Observable.just(storage.load(set));
    }

    override fun loadIdFav(): Observable<IntArray> {
        return Observable.just(storage.loadIdFav());
    }

}
