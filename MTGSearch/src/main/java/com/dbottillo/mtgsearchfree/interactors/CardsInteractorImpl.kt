package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CardsInteractorImpl(private val storage: CardsStorage,
                          private val logger: Logger) : CardsInteractor {

    init {
        logger.d("created")
    }

    override fun getLuckyCards(howMany: Int): Observable<CardsCollection> {
        logger.d("get lucky cards")
        return Observable.fromCallable { storage.getLuckyCards(howMany) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFavourites(): Observable<List<MTGCard>> {
        logger.d("get favourites")
        return Observable.fromCallable { storage.getFavourites() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun saveAsFavourite(card: MTGCard): Observable<IntArray> {
        logger.d("save as favourite")
        return Observable.fromCallable { storage.saveAsFavourite(card) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun removeFromFavourite(card: MTGCard): Observable<IntArray> {
        logger.d("remove from favourite")
        return Observable.fromCallable { storage.removeFromFavourite(card) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadSet(set: MTGSet): Observable<CardsCollection> {
        logger.d("loadSet " + set.toString())
        return Observable.fromCallable { storage.load(set) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadIdFav(): Observable<IntArray> {
        logger.d("loadSet id fav")
        return Observable.fromCallable { storage.loadIdFav() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun doSearch(searchParams: SearchParams): Observable<CardsCollection> {
        logger.d("do search " + searchParams.toString())
        return Observable.fromCallable { storage.doSearch(searchParams) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadCard(multiverseid: Int): Observable<MTGCard> {
        logger.d("loading card with multiverse id: " + multiverseid)
        return Observable.fromCallable { storage.loadCard(multiverseid) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadOtherSideCard(card: MTGCard): Observable<MTGCard> {
        logger.d("loading other side of card: " + card.toString())
        return Observable.fromCallable { storage.loadOtherSide(card) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}

