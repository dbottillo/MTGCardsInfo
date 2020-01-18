package com.dbottillo.mtgsearchfree.interactors

import android.graphics.Bitmap
import android.net.Uri
import com.dbottillo.mtgsearchfree.interactor.SchedulerProvider
import com.dbottillo.mtgsearchfree.model.CardPrice
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.repository.CardRepository
import com.dbottillo.mtgsearchfree.storage.CardsStorage
import com.dbottillo.mtgsearchfree.util.FileManager
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import io.reactivex.Single

class CardsInteractorImpl(
    private val storage: CardsStorage,
    private val fileManager: FileManager,
    private val schedulerProvider: SchedulerProvider,
    private val logger: Logger,
    private val cardRepository: CardRepository
) : CardsInteractor {

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

    override fun saveAsFavourite(card: MTGCard) {
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
        logger.d("loadSet $set")
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
        logger.d("do search $searchParams")
        return Observable.fromCallable { storage.doSearch(searchParams) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun loadCard(multiverseId: Int): Observable<MTGCard> {
        logger.d("loading card with multiverse id: $multiverseId")
        return Observable.fromCallable { storage.loadCard(multiverseId) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun loadCardById(id: Int): Single<MTGCard> {
        logger.d("loading card with id: $id")
        return Single.fromCallable { storage.loadCardById(id) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun loadOtherSideCard(card: MTGCard): Single<MTGCard> {
        logger.d("loading other side of card: $card")
        return Single.fromCallable { storage.loadOtherSide(card) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun getArtworkUri(bitmap: Bitmap): Single<Uri> {
        logger.d("get artwork uri from bitmap")
        return Single.defer<Uri> {
            Single.just(fileManager.saveBitmapToFile(bitmap))
        }.subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun fetchPrice(card: MTGCard): Single<CardPrice> {
        return cardRepository.fetchPriceTCG(card)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }
}
