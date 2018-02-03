package com.dbottillo.mtgsearchfree.interactors

import android.net.Uri
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class DecksInteractorImpl @Inject
constructor(val storage: DecksStorage,
            val fileUtil: FileUtil,
            val schedulerProvider: SchedulerProvider,
            val logger: Logger) : DecksInteractor {

    init {
        logger.d("created")
    }

    override fun load(): Single<List<Deck>> {
        logger.d("loadSet decks")
        return Single.fromCallable { storage.load() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun loadDeck(deck: Deck): Observable<DeckCollection> {
        logger.d("loadSet " + deck.toString())
        return Observable.fromCallable { storage.loadDeck(deck) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun copy(deck: Deck): Single<List<Deck>> {
        logger.d("copy deck")
        return Single.fromCallable { storage.copy(deck) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun addDeck(name: String): Observable<List<Deck>> {
        logger.d("create deck with name: " + name)
        return Observable.fromCallable { storage.addDeck(name) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun deleteDeck(deck: Deck): Observable<List<Deck>> {
        logger.d("delete " + deck.toString())
        return Observable.fromCallable { storage.deleteDeck(deck) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun editDeck(deck: Deck, name: String): Single<Deck> {
        logger.d("edit " + deck.toString() + " with name: " + name)
        return Single.fromCallable { storage.editDeck(deck, name) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun addCard(deck: Deck, card: MTGCard, quantity: Int): Observable<DeckCollection> {
        logger.d("add " + quantity + " " + card.toString() + " to deck: " + deck)
        return Observable.fromCallable { storage.addCard(deck, card, quantity) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun addCard(name: String, card: MTGCard, quantity: Int): Observable<DeckCollection> {
        logger.d("add " + quantity + " " + card.toString() + " to new deck with name: " + name)
        return Observable.fromCallable { storage.addCard(name, card, quantity) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun removeCard(deck: Deck, card: MTGCard): Observable<DeckCollection> {
        logger.d("remove " + card.toString() + " from deck: " + deck)
        return Observable.fromCallable { storage.removeCard(deck, card) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun removeAllCard(deck: Deck, card: MTGCard): Observable<DeckCollection> {
        logger.d("remove all " + card.toString() + " from deck: " + deck)
        return Observable.fromCallable { storage.removeAllCard(deck, card) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun importDeck(uri: Uri): Observable<List<Deck>> {
        logger.d("import " + uri.toString())
        try {
            return Observable.fromCallable { storage.importDeck(uri) }
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
        } catch (throwable: Throwable) {
            return Observable.error<List<Deck>>(throwable)
        }
    }

    override fun exportDeck(deck: Deck): Completable {
        return Completable.fromCallable {
            val cards = storage.loadDeck(deck).allCards()
            val exported = fileUtil.downloadDeckToSdCard(deck, CardsCollection(cards, null, true))
            if (exported){
                Completable.complete()
            } else {
                Completable.error(Throwable("deck not exported"))
            }
        }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun moveCardToSideboard(deck: Deck, card: MTGCard, quantity: Int): Observable<DeckCollection> {
        logger.d("move " + card.toString() + " to sideboard deck: " + deck)
        return Observable.fromCallable { storage.moveCardToSideboard(deck, card, quantity) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun moveCardFromSideboard(deck: Deck, card: MTGCard, quantity: Int): Observable<DeckCollection> {
        logger.d("move " + card.toString() + " from sideboard deck: " + deck)
        return Observable.fromCallable { storage.moveCardFromSideboard(deck, card, quantity) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

}
