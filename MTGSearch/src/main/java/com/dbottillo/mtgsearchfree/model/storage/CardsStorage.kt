package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams

interface CardsStorage{

    fun load(set: MTGSet): List<MTGCard>
    fun saveAsFavourite(card: MTGCard): IntArray
    fun loadIdFav(): IntArray
    fun removeFromFavourite(card: MTGCard): IntArray
    fun getLuckyCards(howMany: Int): List<MTGCard>
    fun getFavourites(): List<MTGCard>
    fun loadDeck(deck: Deck): List<MTGCard>
    fun doSearch(searchParams: SearchParams): List<MTGCard>
    fun loadCard(multiverseId: Int): MTGCard
    fun loadOtherSide(card: MTGCard): MTGCard
}
