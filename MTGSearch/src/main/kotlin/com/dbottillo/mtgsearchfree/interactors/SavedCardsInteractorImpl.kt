package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.SavedCardsStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable

class SavedCardsInteractorImpl(
    private val storage: SavedCardsStorage,
    private val schedulerProvider: SchedulerProvider,
    private val logger: Logger
) : SavedCardsInteractor {

    init {
        logger.d("created")
    }

    override fun load(): Observable<CardsCollection> {
        logger.d("get favourites")
        return Observable.fromCallable { storage.load() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun save(card: MTGCard): Observable<CardsCollection> {
        logger.d("save as favourite")
        return Observable.fromCallable {
            storage.saveAsFavourite(card)
            storage.load() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun remove(card: MTGCard): Observable<CardsCollection> {
        logger.d("remove from favourite")
        return Observable.fromCallable {
            storage.removeFromFavourite(card)
            storage.load() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun loadId(): Observable<IntArray> {
        logger.d("loadSet id fav")
        return Observable.fromCallable { storage.loadIdFav() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }
}
