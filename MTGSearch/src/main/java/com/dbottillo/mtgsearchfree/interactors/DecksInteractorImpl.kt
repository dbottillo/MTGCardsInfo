package com.dbottillo.mtgsearchfree.interactors

import android.net.Uri

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.Logger

import javax.inject.Inject

import io.reactivex.Observable

class DecksInteractorImpl @Inject
constructor(val storage: DecksStorage,
            val fileUtil: FileUtil,
            val logger: Logger) : DecksInteractor {

    init {
        logger.d("created")
    }

    override fun load(): Observable<List<Deck>> {
        logger.d("loadSet decks")
        return Observable.just(storage.load())
    }

    override fun loadDeck(deck: Deck): Observable<CardsCollection> {
        logger.d("loadSet " + deck.toString())
        return Observable.just(storage.loadDeck(deck))
    }

    override fun addDeck(name: String): Observable<List<Deck>> {
        logger.d("create deck with name: " + name)
        return Observable.just(storage.addDeck(name))
    }

    override fun deleteDeck(deck: Deck): Observable<List<Deck>> {
        logger.d("delete " + deck.toString())
        return Observable.just(storage.deleteDeck(deck))
    }

    override fun editDeck(deck: Deck, name: String): Observable<CardsCollection> {
        logger.d("edit " + deck.toString() + " with name: " + name)
        return Observable.just(storage.editDeck(deck, name))
    }

    override fun addCard(deck: Deck, card: MTGCard, quantity: Int): Observable<CardsCollection> {
        logger.d("add " + quantity + " " + card.toString() + " to deck: " + deck)
        return Observable.just(storage.addCard(deck, card, quantity))
    }

    override fun addCard(name: String, card: MTGCard, quantity: Int): Observable<CardsCollection> {
        logger.d("add " + quantity + " " + card.toString() + " to new deck with name: " + name)
        return Observable.just(storage.addCard(name, card, quantity))
    }

    override fun removeCard(deck: Deck, card: MTGCard): Observable<CardsCollection> {
        logger.d("remove " + card.toString() + " from deck: " + deck)
        return Observable.just(storage.removeCard(deck, card))
    }

    override fun removeAllCard(deck: Deck, card: MTGCard): Observable<CardsCollection> {
        logger.d("remove all " + card.toString() + " from deck: " + deck)
        return Observable.just(storage.removeAllCard(deck, card))
    }

    override fun importDeck(uri: Uri): Observable<List<Deck>> {
        logger.d("import " + uri.toString())
        try {
            return Observable.just(storage.importDeck(uri))
        } catch (throwable: Throwable) {
            return Observable.error<List<Deck>>(throwable)
        }
    }

    override fun exportDeck(deck: Deck, cards: CardsCollection): Observable<Boolean> {
        return Observable.just(fileUtil.downloadDeckToSdCard(deck, cards))
    }

    override fun moveCardToSideboard(deck: Deck, card: MTGCard, quantity: Int): Observable<CardsCollection> {
        logger.d("move " + card.toString() + " to sideboard deck: " + deck)
        return Observable.just(storage.moveCardToSideboard(deck, card, quantity))
    }

    override fun moveCardFromSideboard(deck: Deck, card: MTGCard, quantity: Int): Observable<CardsCollection> {
        logger.d("move " + card.toString() + " from sideboard deck: " + deck)
        return Observable.just(storage.moveCardFromSideboard(deck, card, quantity))
    }

}
