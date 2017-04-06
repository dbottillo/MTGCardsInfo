package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.SavedCards

import io.reactivex.Observable

interface SavedCardsInteractor {

    fun save(card: MTGCard): Observable<SavedCards>

    fun remove(card: MTGCard): Observable<SavedCards>

    fun loadId(): Observable<IntArray>

    fun load(): Observable<SavedCards>

}