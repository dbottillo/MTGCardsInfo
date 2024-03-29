package com.dbottillo.mtgsearchfree.storage

import com.dbottillo.mtgsearchfree.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.util.Logger

open class CardsStorageImpl(
    private val mtgCardDataSource: MTGCardDataSource,
    private val favouritesDataSource: FavouritesDataSource,
    private val cardsPreferences: CardsPreferences,
    private val cardsHelper: CardsHelper,
    private val logger: Logger
) : CardsStorage {

    init {
        logger.d("created")
    }

    override fun load(set: MTGSet): CardsCollection {
        logger.d("loadSet $set")
        val cards = mtgCardDataSource.getSet(set)
        val filter = cardsPreferences.load()
        return CardsCollection(cardsHelper.filterAndSortSet(filter = filter, list = cards), filter)
    }

    override fun saveAsFavourite(card: MTGCard) {
        logger.d("save as fav $card")
        favouritesDataSource.saveFavourites(card)
    }

    override fun removeFromFavourite(card: MTGCard) {
        logger.d("remove as fav $card")
        favouritesDataSource.removeFavourites(card)
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

    override fun getLuckyCards(howMany: Int): CardsCollection {
        logger.d("$howMany lucky cards requested")
        return CardsCollection(mtgCardDataSource.getRandomCard(howMany))
    }

    override fun getFavourites(): List<MTGCard> {
        logger.d()
        return favouritesDataSource.getCards(true)
    }

    override fun doSearch(searchParams: SearchParams): CardsCollection {
        logger.d("do search $searchParams")
        val cards = mtgCardDataSource.searchCards(searchParams)
        val filter = cardsPreferences.load()
        filter.sortSetNumber = !searchParams.sortAZ
        return CardsCollection(cardsHelper.sortMultipleSets(filter, cards), null)
    }

    override fun loadCard(multiverseId: Int, fallbackName: String): MTGCard {
        logger.d("do search with multiverse: $multiverseId")
        return mtgCardDataSource.searchCard(multiverseId) // try with name
            ?: return mtgCardDataSource.searchCard(name = fallbackName, requiredMultiverseId = true)
                ?: throw UnsupportedOperationException("can't find card with multi-verse id $multiverseId")
    }

    override fun loadCardById(id: Int): MTGCard {
        logger.d("do search with id: $id")
        return mtgCardDataSource.searchCardById(id)
            ?: throw UnsupportedOperationException("can't find card with id $id")
    }

    override fun loadOtherSide(card: MTGCard): MTGCard {
        logger.d("do search other side card $card")
        if (card.otherFaceIds.size > 0) {
            val otherCard = mtgCardDataSource.searchCardByUUID(card.otherFaceIds[0])
            if (otherCard != null) {
                return otherCard
            }
        }
        if (card.names.size < 2) {
            return card
        }
        var name = card.names[0]
        if (name.equals(card.name, ignoreCase = true)) {
            name = card.names[1]
        }
        return mtgCardDataSource.searchCard(name)
            ?: throw UnsupportedOperationException("can't find other side of $card")
    }
}
