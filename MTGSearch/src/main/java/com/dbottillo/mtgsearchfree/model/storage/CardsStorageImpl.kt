package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.*
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource
import com.dbottillo.mtgsearchfree.model.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.view.helpers.CardsHelper
import java.util.*

open class CardsStorageImpl(private val mtgCardDataSource: MTGCardDataSource,
                            private val favouritesDataSource: FavouritesDataSource,
                            private val cardsPreferences: CardsPreferences,
                            private val cardsHelper: CardsHelper,
                            private val logger: Logger) : CardsStorage {

    init {
        logger.d("created")
    }

    override fun load(set: MTGSet): CardsCollection {
        logger.d("loadSet " + set)
        val cards = mtgCardDataSource.getSet(set)
        val filter = cardsPreferences.load()
        return CardsCollection(cardsHelper.filterCards(filter, cards), filter)
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

    override fun getLuckyCards(howMany: Int): List<MTGCard> {
        logger.d(howMany.toString() + " lucky cards requested")
        return mtgCardDataSource.getRandomCard(howMany)
    }

    override fun getFavourites(): List<MTGCard> {
        logger.d()
        return favouritesDataSource.getCards(true)
    }

    override fun doSearch(searchParams: SearchParams): CardsCollection {
        logger.d("do search " + searchParams)
        val cards = mtgCardDataSource.searchCards(searchParams)
        val filter = cardsPreferences.load()
        cardsHelper.sortCards(filter, cards)
        return CardsCollection(cards, null)
    }

    override fun loadCard(multiverseId: Int): MTGCard {
        logger.d("do search with multiverse: " + multiverseId)
        return mtgCardDataSource.searchCard(multiverseId)
    }

    override fun loadOtherSide(card: MTGCard): MTGCard {
        logger.d("do search other side card " + card.toString())
        if (card.names == null) {
            return card
        }
        if (card.names.size < 2) {
            return card
        }
        var name = card.names[0]
        if (name.equals(card.name, ignoreCase = true)) {
            name = card.names[1]
        }
        return mtgCardDataSource.searchCard(name)
    }
}
