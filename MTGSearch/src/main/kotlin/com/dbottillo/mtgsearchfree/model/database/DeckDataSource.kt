package com.dbottillo.mtgsearchfree.model.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import com.dbottillo.mtgsearchfree.model.CardsBucket
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.LOG

import java.util.ArrayList

class DeckDataSource(
    private val database: SQLiteDatabase,
    private val cardDataSource: CardDataSource,
    private val mtgCardDataSource: MTGCardDataSource
) {

    val decks: List<Deck>
        get() {
            val decks = ArrayList<Deck>()
            val query = "Select * from decks"
            LOG.query(query)
            val deckCursor = database.rawQuery(query, null)
            deckCursor.moveToFirst()
            while (!deckCursor.isAfterLast) {
                val newDeck = fromCursor(deckCursor)
                decks.add(newDeck)
                deckCursor.moveToNext()
            }
            deckCursor.close()
            setQuantityOfCards(decks, false)
            setQuantityOfCards(decks, true)
            return decks
        }

    fun addDeck(name: String): Long {
        val values = ContentValues()
        values.put(COLUMNS.NAME.noun, name)
        values.put(COLUMNS.ARCHIVED.noun, 0)
        return database.insert(TABLE, null, values)
    }

    fun addDeck(bucket: CardsBucket): Long {
        val deckId = addDeck(bucket.key)
        if (bucket.cards.isEmpty()) {
            return deckId
        }
        for (card in bucket.cards) {
            val realCard = mtgCardDataSource.searchCard(card.name)
            if (realCard != null) {
                realCard.isSideboard = card.isSideboard
                addCardToDeck(deckId, realCard, card.quantity)
            }
        }
        return deckId
    }

    fun addCardToDeck(deckId: Long, card: MTGCard, quantity: Int) {
        val sid = if (card.isSideboard) 1 else 0
        var currentCard = 0
        val query = "select H.*,P.* from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ? and P.multiVerseId = ? and H.side == ?)"
        LOG.query(query, deckId.toString() + "", card.multiVerseId.toString() + "", sid.toString() + "")
        val cardsCursor = database.rawQuery(query, arrayOf(deckId.toString() + "", card.multiVerseId.toString() + "", sid.toString() + ""))
        if (cardsCursor.count > 0) {
            LOG.d("card already in the database")
            cardsCursor.moveToFirst()
            currentCard = cardsCursor.getInt(cardsCursor.getColumnIndex(COLUMNSJOIN.QUANTITY.noun))
        }
        if (currentCard + quantity <= 0) {
            LOG.d("the quantity is negative and is bigger than the current quantity so needs to be removed")
            cardsCursor.close()
            removeCardFromDeck(deckId, card)
            return
        }
        if (currentCard > 0) {
            // there is already some cards there! just need to add the quantity
            LOG.d("just need to update the quantity")
            updateQuantity(deckId, currentCard + quantity, card.multiVerseId, sid)
            cardsCursor.close()
            return
        }
        cardsCursor.close()
        addCardToDeckWithoutCheck(deckId, card, quantity)
    }

    fun addCardToDeckWithoutCheck(deckId: Long, card: MTGCard, quantity: Int) {
        val query = "select * from MTGCard where multiVerseId=?"
        LOG.query(query, card.multiVerseId.toString() + "")
        val current = database.rawQuery(query, arrayOf(card.multiVerseId.toString() + ""))
        if (current.count > 0) {
            // card already added
            if (current.count > 1) {
                // there is a duplicate
                current.moveToFirst()
                current.moveToNext()
                while (!current.isAfterLast) {
                    val query2 = "delete from MTGCard where _id=?"
                    LOG.query(query2, current.getString(0))
                    val cursor = database.rawQuery(query2, arrayOf(current.getString(0)))
                    cursor.moveToFirst()
                    cursor.close()
                    current.moveToNext()
                }
            }
        } else {
            // need to add the card
            cardDataSource.saveCard(card)
        }
        current.close()
        val values = ContentValues()
        values.put(COLUMNSJOIN.CARD_ID.noun, card.multiVerseId)
        values.put(COLUMNSJOIN.DECK_ID.noun, deckId)
        values.put(COLUMNSJOIN.QUANTITY.noun, quantity)
        values.put(COLUMNSJOIN.SIDE.noun, if (card.isSideboard) 1 else 0)
        database.insert(TABLE_JOIN, null, values)
    }

    fun getDeck(deckId: Long): Deck {
        val query = "select * from $TABLE where rowid =?"
        val cursor = database.rawQuery(query, arrayOf(deckId.toString() + ""))
        LOG.query(query)
        cursor.moveToFirst()
        val deck = fromCursor(cursor)
        cursor.close()
        return deck
    }

    private fun setQuantityOfCards(decks: ArrayList<Deck>, side: Boolean) {
        // need to loadSet cards now
        // select SUM(quantity),* from deck_card DC left join decks D on (D._id = DC.deck_id) where side= 0 group by DC.deck_id
        // Cursor cursor = database.rawQuery("Select * from decks D left join deck_card DC on (D._id = DC.deck_id) where DC.side=0", null);
        // Cursor cursor = database.rawQuery("select SUM(quantity),D._id from deck_card DC left join decks D on (D._id = DC.deck_id) where side= 0 group by DC.deck_id", null);
        val query = "select SUM(quantity),D._id from deck_card DC left join decks D on (D._id = DC.deck_id) where side=" + (if (side) 1 else 0) + " group by DC.deck_id"
        LOG.query(query)
        val cursor = database.rawQuery(query, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            for (deck in decks) {
                if (deck.id == cursor.getInt(1).toLong()) {
                    if (side) {
                        deck.sizeOfSideboard = cursor.getInt(0)
                    } else {
                        deck.numberOfCards = cursor.getInt(0)
                    }
                    break
                }
            }
            cursor.moveToNext()
        }
        cursor.close()
    }

    fun getCards(deck: Deck): List<MTGCard> {
        return getCards(deck.id)
    }

    fun getCards(deckId: Long): List<MTGCard> {
        val cards = ArrayList<MTGCard>()
        val query = "select H.*,P.* from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ?)"
        LOG.query(query, deckId.toString() + "")
        val cursor = database.rawQuery(query, arrayOf(deckId.toString() + ""))

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val card = cardDataSource.fromCursor(cursor)
            val quantity = cursor.getInt(cursor.getColumnIndex(COLUMNSJOIN.QUANTITY.noun))
            card.quantity = quantity
            val sideboard = cursor.getInt(cursor.getColumnIndex(COLUMNSJOIN.SIDE.noun))
            card.isSideboard = sideboard == 1
            cards.add(card)
            cursor.moveToNext()
        }
        cursor.close()
        return cards
    }

    fun removeCardFromDeck(deckId: Long, card: MTGCard) {
        val sid = if (card.isSideboard) 1 else 0
        val args = arrayOf(deckId.toString() + "", card.multiVerseId.toString() + "", sid.toString() + "")
        val query = "DELETE FROM deck_card where deck_id=? and card_id=? and side =?"
        LOG.query(query, *args)
        val cursor = database.rawQuery(query, args)
        cursor.moveToFirst()
        cursor.close()
    }

    fun deleteDeck(deck: Deck) {
        val args = arrayOf(deck.id.toString() + "")
        val query = "DELETE FROM deck_card where deck_id=? "
        LOG.query(query, *args)
        val cursor = database.rawQuery(query, args)
        cursor.moveToFirst()
        cursor.close()
        val query2 = "DELETE FROM decks where _id=? "
        val cursor2 = database.rawQuery(query2, args)
        cursor2.moveToFirst()
        cursor2.close()
    }

    fun deleteAllDecks(db: SQLiteDatabase) {
        val query = "DELETE FROM deck_card"
        LOG.query(query)
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        cursor.close()
        val query2 = "DELETE FROM decks"
        LOG.query(query2)
        val cursor2 = db.rawQuery(query2, null)
        cursor2.moveToFirst()
        cursor2.close()
    }

    fun copy(deck: Deck) {
        database.beginTransaction()
        try {
            val copyDeckId = addDeck(deck.name + " copy")
            val cards = getCards(deck)
            for (card in cards) {
                addCardToDeck(copyDeckId, card, card.quantity)
            }
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            LOG.e(e)
        } finally {
            database.endTransaction()
        }
    }

    fun moveCardToSideBoard(deckId: Long, card: MTGCard, quantity: Int) {
        moveCardInDeck(deckId, card, quantity, true)
    }

    fun moveCardFromSideBoard(deckId: Long, card: MTGCard, quantity: Int) {
        moveCardInDeck(deckId, card, quantity, false)
    }

    private fun moveCardInDeck(deckId: Long, card: MTGCard, quantity: Int, fromDeckToSide: Boolean) {
        var removeCard = false
        val before = if (fromDeckToSide) 0 else 1
        val after = if (fromDeckToSide) 1 else 0

        val cursor = runQuery("select quantity from deck_card where deck_id=? and card_id=? and side = ?",
                deckId.toString(), card.multiVerseId.toString(), before.toString())
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) - quantity <= 0) {
                removeCard = true
            } else {
                updateQuantity(deckId, cursor.getInt(0) - quantity, card.multiVerseId, before)
            }
        }
        cursor.close()

        val cursorSideboard = runQuery("select quantity from deck_card where deck_id=? and card_id=? and side = ?",
                deckId.toString(), card.multiVerseId.toString(), after.toString())
        if (cursorSideboard.moveToFirst()) {
            updateQuantity(deckId, cursorSideboard.getInt(0) + quantity, card.multiVerseId, after)
        } else {
            // card wasn't in the deck
            card.isSideboard = before == 0
            addCardToDeckWithoutCheck(deckId, card, quantity)
        }
        cursorSideboard.close()

        if (removeCard) {
            card.isSideboard = before == 1
            removeCardFromDeck(deckId, card)
        }
    }

    private fun updateQuantity(deckId: Long, quantity: Int, multiverseId: Int, sid: Int) {
        val values = ContentValues()
        values.put(COLUMNSJOIN.QUANTITY.noun, quantity)
        val query = ("UPDATE " + TABLE_JOIN + " SET quantity=? WHERE " + COLUMNSJOIN.DECK_ID.noun + " = ? and " +
                COLUMNSJOIN.CARD_ID.noun + " = ? and " + COLUMNSJOIN.SIDE.noun + " = ?")
        val args = arrayOf(quantity.toString(), deckId.toString() + "", multiverseId.toString() + "", sid.toString() + "")
        val cursor = runQuery(query, *args)
        cursor.moveToFirst()
        cursor.close()
    }

    fun renameDeck(deckId: Long, name: String): Int {
        val contentValues = ContentValues()
        contentValues.put(COLUMNS.NAME.noun, name)
        return database.update(TABLE, contentValues, "_id=$deckId", null)
    }

    fun fromCursor(cursor: Cursor): Deck {
        val deck = Deck(cursor.getLong(cursor.getColumnIndex("_id")))
        deck.name = cursor.getString(cursor.getColumnIndex(COLUMNS.NAME.noun))
        deck.isArchived = cursor.getInt(cursor.getColumnIndex(COLUMNS.ARCHIVED.noun)) == 1
        return deck
    }

    private fun runQuery(query: String, vararg args: String): Cursor {
        val cursor = database.rawQuery(query, args)
        LOG.query(query, *args)
        return cursor
    }

    private fun runQueryAndClose(query: String, vararg args: String) {
        runQuery(query, *args).close()
    }

    private enum class COLUMNS(val noun: String, val type: String) {
        NAME("name", "TEXT not null"),
        COLOR("color", "TEXT"),
        ARCHIVED("archived", "INT")
    }

    private enum class COLUMNSJOIN(val noun: String, val type: String) {
        DECK_ID("deck_id", "INT not null"),
        CARD_ID("card_id", "INT not null"),
        QUANTITY("quantity", "INT not null"),
        SIDE("side", "INT")
    }

    companion object {

        const val TABLE = "decks"
        const val TABLE_JOIN = "deck_card"

        fun generateCreateTable(): String {
            val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ")
            builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT, ")
            for (column in COLUMNS.values()) {
                builder.append(column.noun).append(' ').append(column.type)
                if (column != COLUMNS.ARCHIVED) {
                    builder.append(',')
                }
            }
            builder.append(')')
            return builder.toString()
        }

        fun generateCreateTableJoin(): String {
            val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ")
            builder.append(TABLE_JOIN).append(" (")
            for (column in COLUMNSJOIN.values()) {
                builder.append(column.noun).append(' ').append(column.type)
                if (column != COLUMNSJOIN.SIDE) {
                    builder.append(',')
                }
            }
            builder.append(')')
            return builder.toString()
        }
    }
}
