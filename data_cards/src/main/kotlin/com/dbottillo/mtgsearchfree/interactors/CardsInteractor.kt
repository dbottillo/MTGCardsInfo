package com.dbottillo.mtgsearchfree.interactors

import android.graphics.Bitmap
import android.net.Uri
import com.dbottillo.mtgsearchfree.model.CardPrice
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.PriceProvider
import com.dbottillo.mtgsearchfree.model.SearchParams
import io.reactivex.Observable
import io.reactivex.Single

interface CardsInteractor {
    fun loadSet(set: MTGSet): Observable<CardsCollection>
    fun saveAsFavourite(card: MTGCard)
    fun removeFromFavourite(card: MTGCard)
    fun loadIdFav(): Observable<IntArray>
    fun getLuckyCards(howMany: Int): Observable<CardsCollection>
    fun getFavourites(): Observable<List<MTGCard>>
    fun doSearch(searchParams: SearchParams): Observable<CardsCollection>
    fun loadCardById(id: Int): Single<MTGCard>
    fun loadOtherSideCard(card: MTGCard): Single<MTGCard>
    fun getArtworkUri(bitmap: Bitmap): Single<Uri>
    fun fetchPrice(card: MTGCard, priceProvider: PriceProvider): Single<CardPrice>
}