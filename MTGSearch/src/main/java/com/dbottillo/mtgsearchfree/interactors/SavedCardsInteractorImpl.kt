package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.SavedCards
import com.dbottillo.mtgsearchfree.model.storage.SavedCardsStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable


class SavedCardsInteractorImpl(private val storage: SavedCardsStorage,
                               private val logger: Logger) : SavedCardsInteractor {

    init {
        logger.d("created")
    }

    override fun load(): Observable<SavedCards> {
        logger.d("get favourites")
         return Observable.fromCallable {
             storage.load()
         }
    }

    override fun save(card: MTGCard): Observable<SavedCards>  {
        logger.d("save as favourite")
        return Observable.fromCallable {
            storage.saveAsFavourite(card)
            storage.load()
        }
    }

    override fun remove(card: MTGCard): Observable<SavedCards>  {
        logger.d("remove from favourite")
        return Observable.fromCallable {
            storage.removeFromFavourite(card)
            storage.load()
        }
    }

    override fun loadId(): Observable<IntArray> {
        logger.d("loadSet id fav")
        return Observable.just(storage.loadIdFav())
    }
}

