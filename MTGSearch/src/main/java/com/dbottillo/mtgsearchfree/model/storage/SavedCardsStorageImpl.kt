package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.util.Logger

class SavedCardsStorageImpl(private val favouritesDataSource: FavouritesDataSource,
                            private val cardsHelper: CardsHelper,
                            private val cardsPreferences: CardsPreferences,
                            private val logger: Logger) : SavedCardsStorage {

    init {
        logger.d("created")
    }

    override fun saveAsFavourite(card: MTGCard): IntArray {
        logger.d("save as fav " + card)
        favouritesDataSource.saveFavourites(card)
        return loadIdFav()
    }

    override fun loadIdFav(): IntArray {
        logger.d()
        val cards = favouritesDataSource.getCards(false)
        val result = IntArray(cards.size)
        for (i in cards.indices) {
            result[i] = cards[i].multiVerseId
        }
        return result
    }

    override fun removeFromFavourite(card: MTGCard): IntArray {
        logger.d("remove as fav " + card)
        favouritesDataSource.removeFavourites(card)
        return loadIdFav()
    }

    override fun load(): CardsCollection {
        logger.d()
        val filter = cardsPreferences.load()
        val cards = cardsHelper.filterCards(filter = filter, list =  favouritesDataSource.getCards(true))
        return CardsCollection(cards, filter)
    }
}
