package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams

interface CardsStorage {
    fun load(set: MTGSet): CardsCollection
    fun saveAsFavourite(card: MTGCard)
    fun removeFromFavourite(card: MTGCard)
    fun loadIdFav(): IntArray
    fun getLuckyCards(howMany: Int): CardsCollection
    fun getFavourites(): List<MTGCard>
    fun doSearch(searchParams: SearchParams): CardsCollection
    fun loadCard(multiverseId: Int): MTGCard
    fun loadOtherSide(card: MTGCard): MTGCard
    fun loadCardById(id: Int): MTGCard
}
