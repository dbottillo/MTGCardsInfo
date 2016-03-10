package com.dbottillo.mtgsearchfree.model.storage

import android.content.Context
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet

class CardsStorage(var context: Context) {

    fun load(set: MTGSet): List<MTGCard> {
        var helper = MTGDatabaseHelper(context)
        return helper.getSet(set)
    }

    fun saveAsFavourite(card: MTGCard) {
        FavouritesDataSource.saveFavourites(CardsInfoDbHelper.getInstance(context).writableDatabase, card)
    }

}
