package com.dbottillo.mtgsearchfree.model.storage

import android.content.Context
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import java.util.*

class CardsStorage(var context: Context) {

    fun load(set: MTGSet): ArrayList<MTGCard> {
        var helper = MTGDatabaseHelper(context)
        return helper.getSet(set)
    }

    fun saveAsFavourite(card: MTGCard): IntArray {
        FavouritesDataSource.saveFavourites(CardsInfoDbHelper.getInstance(context).writableDatabase, card)
        return loadIdFav()
    }

    fun loadIdFav(): IntArray {
        var helper = CardsInfoDbHelper.getInstance(context);
        var cards = FavouritesDataSource.getCards(helper.readableDatabase, false);
        var result = IntArray(cards.size)
        var index = 0
        cards.forEach {
            result[index] = it.multiVerseId
            index++
        }
        return result
    }

    fun removeFromFavourite(card: MTGCard): IntArray {
        FavouritesDataSource.removeFavourites(CardsInfoDbHelper.getInstance(context).writableDatabase, card)
        return loadIdFav()
    }

    fun getLuckyCards(howMany: Int): ArrayList<MTGCard>? {
        var helper = MTGDatabaseHelper(context)
        return helper.getRandomCard(howMany)
    }

}
