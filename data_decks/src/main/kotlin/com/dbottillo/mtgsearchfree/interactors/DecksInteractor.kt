package com.dbottillo.mtgsearchfree.interactors

import android.net.Uri
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import io.reactivex.Observable
import io.reactivex.Single

interface DecksInteractor {
    fun load(): Single<List<Deck>>
    fun loadDeck(deckId: Long): Observable<DeckCollection>
    fun loadDeckById(deckId: Long): Single<Deck>
    fun addDeck(name: String): Observable<List<Deck>>
    fun deleteDeck(deck: Deck): Observable<List<Deck>>
    fun editDeck(deck: Deck, name: String): Single<Deck>
    fun addCard(name: String, card: MTGCard, quantity: Int): Observable<DeckCollection>
    fun addCard(deck: Deck, card: MTGCard, quantity: Int): Observable<DeckCollection>
    fun removeCard(deck: Deck, card: MTGCard): Observable<DeckCollection>
    fun removeAllCard(deck: Deck, card: MTGCard): Observable<DeckCollection>
    fun importDeck(uri: Uri): Observable<List<Deck>>
    fun exportDeck(deck: Deck): Single<Uri>
    fun moveCardToSideboard(deck: Deck, card: MTGCard, quantity: Int): Observable<DeckCollection>
    fun moveCardFromSideboard(deck: Deck, card: MTGCard, quantity: Int): Observable<DeckCollection>
    fun copy(deck: Deck): Single<List<Deck>>
}