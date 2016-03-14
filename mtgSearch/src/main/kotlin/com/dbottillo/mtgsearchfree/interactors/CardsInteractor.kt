package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import rx.Observable
import java.util.*

interface CardsInteractor {

    fun load(set: MTGSet): Observable<ArrayList<MTGCard>>

    fun saveAsFavourite(card: MTGCard): Observable<IntArray>

    fun removeFromFavourite(card: MTGCard): Observable<IntArray>

    fun loadIdFav(): Observable<IntArray>

    fun getLuckyCards(howMany: Int): Observable<ArrayList<MTGCard>>
}