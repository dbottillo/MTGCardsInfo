package com.dbottillo.mtgsearchfree.interactors


import android.net.Uri

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard

import io.reactivex.Observable


interface DecksInteractor {

    fun load(): Observable<List<Deck>>

    fun loadDeck(deck: Deck): Observable<CardsCollection>

    fun addDeck(name: String): Observable<List<Deck>>

    fun deleteDeck(deck: Deck): Observable<List<Deck>>

    fun editDeck(deck: Deck, name: String): Observable<CardsCollection>

    fun addCard(name: String, card: MTGCard, quantity: Int): Observable<CardsCollection>

    fun addCard(deck: Deck, card: MTGCard, quantity: Int): Observable<CardsCollection>

    fun removeCard(deck: Deck, card: MTGCard): Observable<CardsCollection>

    fun removeAllCard(deck: Deck, card: MTGCard): Observable<CardsCollection>

    fun importDeck(uri: Uri): Observable<List<Deck>>

    fun exportDeck(deck: Deck, cards: CardsCollection): Observable<Boolean>

    fun moveCardToSideboard(deck: Deck, card: MTGCard, quantity: Int): Observable<CardsCollection>

    fun moveCardFromSideboard(deck: Deck, card: MTGCard, quantity: Int): Observable<CardsCollection>
}