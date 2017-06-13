package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.*

interface CardsStorage{

    fun load(set: MTGSet): CardsCollection
    fun saveAsFavourite(card: MTGCard): IntArray
    fun loadIdFav(): IntArray
    fun removeFromFavourite(card: MTGCard): IntArray
    fun getLuckyCards(howMany: Int): List<MTGCard>
    fun getFavourites(): List<MTGCard>
    fun doSearch(searchParams: SearchParams): CardsCollection
    fun loadCard(multiverseId: Int): MTGCard
    fun loadOtherSide(card: MTGCard): MTGCard
}
