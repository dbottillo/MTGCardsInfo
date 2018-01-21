package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.CardsCollection

import io.reactivex.Observable

interface SavedCardsInteractor {
    fun save(card: MTGCard): Observable<CardsCollection>
    fun remove(card: MTGCard): Observable<CardsCollection>
    fun loadId(): Observable<IntArray>
    fun load(): Observable<CardsCollection>
}