package com.dbottillo.mtgsearchfree.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import java.util.HashSet

/**
 * Helper for access the user database that contains:
 * - cards (that are in decks and favourites)
 * - decks
 * - favourites
 * - players (for life counter)
 */
class CardsInfoDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CardDataSource.generateCreateTable())
        db.execSQL(DeckDataSource.generateCreateTable())
        db.execSQL(DeckDataSource.generateCreateTableJoin())
        db.execSQL(PlayerDataSource.generateCreateTable())
        db.execSQL(FavouritesDataSource.generateCreateTable())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3 && newVersion >= 3) {
            db.execSQL(PlayerDataSource.generateCreateTable())
            db.execSQL(FavouritesDataSource.generateCreateTable())
        }
        if (oldVersion < 4 && newVersion >= 4) {
            db.execSQL(DeckDataSource.generateCreateTable())
            db.execSQL(DeckDataSource.generateCreateTableJoin())
        }
        val columns = readColumnTable(db, CardDataSource.TABLE)
        if (!columns.contains(CardDataSource.COLUMNS.LAYOUT.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_LAYOUT)
        }
        if (!columns.contains(CardDataSource.COLUMNS.RULINGS.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_RULINGS)
        }
        if (!columns.contains(CardDataSource.COLUMNS.SET_CODE.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_SET_CODE)
        }
        if (!columns.contains(CardDataSource.COLUMNS.NUMBER.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_NUMBER)
        }
        if (!columns.contains(CardDataSource.COLUMNS.NAMES.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_NAMES)
        }
        if (!columns.contains(CardDataSource.COLUMNS.SUPER_TYPES.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_SUPER_TYPES)
        }
        if (!columns.contains(CardDataSource.COLUMNS.FLAVOR.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_FLAVOR)
        }
        if (!columns.contains(CardDataSource.COLUMNS.ARTIST.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_ARTIST)
        }
        if (!columns.contains(CardDataSource.COLUMNS.LOYALTY.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_LOYALTY)
        }
        if (!columns.contains(CardDataSource.COLUMNS.PRINTINGS.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_PRINTINGS)
        }
        if (!columns.contains(CardDataSource.COLUMNS.LEGALITIES.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_LEGALITIES)
        }
        if (!columns.contains(CardDataSource.COLUMNS.ORIGINAL_TEXT.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_ORIGINAL_TEXT)
        }
        if (!columns.contains(CardDataSource.COLUMNS.COLORS_IDENTITY.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_COLORS_IDENTITY)
        }
        if (!columns.contains(CardDataSource.COLUMNS.UUID.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_UUID)
        }
        if (!columns.contains(CardDataSource.COLUMNS.SCRYFALLID.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_SCRYFALLID)
        }
        if (!columns.contains(CardDataSource.COLUMNS.TCG_PLAYER_PRODUCT_ID.noun)) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_TCG_PLAYER_PRODUCT_ID)
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    @Synchronized
    fun clear() {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS '" + CardDataSource.TABLE + "'")
        db.execSQL("DROP TABLE IF EXISTS '" + DeckDataSource.TABLE + "'")
        db.execSQL("DROP TABLE IF EXISTS '" + DeckDataSource.TABLE + "'")
        db.execSQL("DROP TABLE IF EXISTS '" + DeckDataSource.TABLE_JOIN + "'")
        db.execSQL("DROP TABLE IF EXISTS '" + PlayerDataSource.TABLE + "'")
        db.execSQL("DROP TABLE IF EXISTS '" + FavouritesDataSource.TABLE + "'")
    }

    fun readColumnTable(db: SQLiteDatabase, table: String): Set<String> {
        val dbCursor = db.rawQuery("PRAGMA table_info($table)", null)
        val columns = HashSet<String>(dbCursor.count)
        if (dbCursor.moveToFirst()) {
            do {
                columns.add(dbCursor.getString(1))
            } while (dbCursor.moveToNext())
        }
        dbCursor.close()
        return columns
    }

    companion object {
        const val DATABASE_NAME = "cardsinfo.db"
    }
}

private const val DATABASE_VERSION = 10