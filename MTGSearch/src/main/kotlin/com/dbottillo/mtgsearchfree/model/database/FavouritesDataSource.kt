package com.dbottillo.mtgsearchfree.model.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.LOG

import java.util.ArrayList

class FavouritesDataSource(private val database: SQLiteDatabase,
                           private val cardDataSource: CardDataSource) {


    fun saveFavourites(card: MTGCard): Long {
        LOG.d("saving " + card.toString() + " as favourite")
        val current = database.rawQuery("select * from MTGCard where multiVerseId=?", arrayOf(card.multiVerseId.toString() + ""))
        if (current.count == 0) {
            // need to add the card
            cardDataSource.saveCard(card)
        }
        current.close()
        val contentValues = ContentValues()
        contentValues.put("_id", card.multiVerseId)
        return database.insertWithOnConflict(TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getCards(fullCard: Boolean): List<MTGCard> {
        LOG.d("get cards, flag full: $fullCard")
        val cards = ArrayList<MTGCard>()
        val query = "select P.* from MTGCard P inner join Favourites H on (H._id = P.multiVerseId)"
        LOG.query(query)
        val cursor = database.rawQuery(query, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            cards.add(cardDataSource.fromCursor(cursor, fullCard))
            cursor.moveToNext()
        }
        cursor.close()
        return cards
    }

    fun removeFavourites(card: MTGCard) {
        LOG.d("remove card  " + card.toString() + " from favourites")
        val args = arrayOf(card.multiVerseId.toString() + "")
        val query = "DELETE FROM $TABLE where _id=? "
        LOG.query(query)
        val cursor = database.rawQuery(query, args)
        cursor.moveToFirst()
        cursor.close()
    }

    fun clear() {
        val query = "DELETE FROM $TABLE"
        LOG.query(query)
        val cursor = database.rawQuery(query, null)
        cursor.moveToFirst()
        cursor.close()
    }

    companion object {

        const val TABLE = "Favourites"

        fun generateCreateTable(): String {
            val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ")
            builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY)")
            return builder.toString()
        }
    }
}
