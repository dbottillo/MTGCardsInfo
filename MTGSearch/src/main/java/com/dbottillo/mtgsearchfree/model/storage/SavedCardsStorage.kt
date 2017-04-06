package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.SavedCards
import com.dbottillo.mtgsearchfree.model.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.util.Logger

interface SavedCardsStorage{

    fun saveAsFavourite(card: MTGCard): IntArray

    fun loadIdFav(): IntArray

    fun removeFromFavourite(card: MTGCard): IntArray

    fun load(): SavedCards
}
