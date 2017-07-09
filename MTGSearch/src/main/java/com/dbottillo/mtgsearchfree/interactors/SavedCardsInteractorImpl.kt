package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.SavedCardsStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SavedCardsInteractorImpl(private val storage: SavedCardsStorage,
                               private val logger: Logger) : SavedCardsInteractor {

    init {
        logger.d("created")
    }

    override fun load(): Observable<CardsCollection> {
        logger.d("get favourites")
        return Observable.fromCallable { storage.load() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun save(card: MTGCard): Observable<CardsCollection> {
        logger.d("save as favourite")
        return Observable.fromCallable {
            storage.saveAsFavourite(card)
            storage.load() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun remove(card: MTGCard): Observable<CardsCollection> {
        logger.d("remove from favourite")
        return Observable.fromCallable {
            storage.removeFromFavourite(card)
            storage.load() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadId(): Observable<IntArray> {
        logger.d("loadSet id fav")
        return Observable.fromCallable { storage.loadIdFav() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}

