package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams

import io.reactivex.Observable

interface CardsInteractor {

    fun loadSet(set: MTGSet): Observable<CardsCollection>

    fun saveAsFavourite(card: MTGCard): Observable<IntArray>

    fun removeFromFavourite(card: MTGCard): Observable<IntArray>

    fun loadIdFav(): Observable<IntArray>

    fun getLuckyCards(howMany: Int): Observable<CardsCollection>

    fun getFavourites(): Observable<List<MTGCard>>

    fun doSearch(searchParams: SearchParams): Observable<CardsCollection>

    fun loadCard(multiverseid: Int): Observable<MTGCard>

    fun loadOtherSideCard(card: MTGCard): Observable<MTGCard>
}