package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable

class CardsInteractorImpl(private val storage: CardsStorage,
                          private val schedulerProvider: SchedulerProvider,
                          private val logger: Logger) : CardsInteractor {

    init {
        logger.d("created")
    }

    override fun getLuckyCards(howMany: Int): Observable<CardsCollection> {
        logger.d("get lucky cards")
        return Observable.fromCallable { storage.getLuckyCards(howMany) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun getFavourites(): Observable<List<MTGCard>> {
        logger.d("get favourites")
        return Observable.fromCallable { storage.getFavourites() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun saveAsFavourite(card: MTGCard){
        logger.d("save as favourite")
        Observable.fromCallable { storage.saveAsFavourite(card) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe()
    }

    override fun removeFromFavourite(card: MTGCard) {
        logger.d("remove from favourite")
        Observable.fromCallable { storage.removeFromFavourite(card) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe()
    }

    override fun loadSet(set: MTGSet): Observable<CardsCollection> {
        logger.d("loadSet " + set.toString())
        return Observable.fromCallable { storage.load(set) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun loadIdFav(): Observable<IntArray> {
        logger.d("loadSet id fav")
        return Observable.fromCallable { storage.loadIdFav() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun doSearch(searchParams: SearchParams): Observable<CardsCollection> {
        logger.d("do search " + searchParams.toString())
        return Observable.fromCallable { storage.doSearch(searchParams) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun loadCard(multiverseId: Int): Observable<MTGCard> {
        logger.d("loading card with multiverse id: " + multiverseId)
        return Observable.fromCallable { storage.loadCard(multiverseId) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun loadOtherSideCard(card: MTGCard): Observable<MTGCard> {
        logger.d("loading other side of card: " + card.toString())
        return Observable.fromCallable { storage.loadOtherSide(card) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }
}

