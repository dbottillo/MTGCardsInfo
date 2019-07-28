package com.dbottillo.mtgsearchfree.debug

import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.database.DeckDataSource
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource
import io.reactivex.Completable

internal class DebugInteractor constructor(
    val cardsInfoDbHelper: CardsInfoDbHelper
) {

    internal fun deleteSavedCards(): Completable {
        return Completable.defer {
            Completable.create {
                val cursor = cardsInfoDbHelper.writableDatabase.rawQuery("delete from ${FavouritesDataSource.TABLE}", null)
                cursor.moveToFirst()
                cursor.close()
                it.onComplete()
            }
        }
    }

    internal fun deleteDecks(): Completable {
        return Completable.defer {
            Completable.create {
                val cursor = cardsInfoDbHelper.writableDatabase.rawQuery("delete from ${DeckDataSource.TABLE}", null)
                cursor.moveToFirst()
                cursor.close()
                val cursor2 = cardsInfoDbHelper.writableDatabase.rawQuery("delete from ${DeckDataSource.TABLE_JOIN}", null)
                cursor2.moveToFirst()
                cursor2.close()
                it.onComplete()
            }
        }
    }
}