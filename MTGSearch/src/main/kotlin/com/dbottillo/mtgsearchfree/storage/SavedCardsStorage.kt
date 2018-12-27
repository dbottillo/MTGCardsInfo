package com.dbottillo.mtgsearchfree.storage

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.CardsCollection

interface SavedCardsStorage {
    fun saveAsFavourite(card: MTGCard): IntArray
    fun loadIdFav(): IntArray
    fun removeFromFavourite(card: MTGCard): IntArray
    fun load(): CardsCollection
}
