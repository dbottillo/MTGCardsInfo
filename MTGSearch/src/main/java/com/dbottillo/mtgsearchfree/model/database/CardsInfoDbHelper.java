package com.dbottillo.mtgsearchfree.model.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper for access the use database that contains:
 * - cards (that are in decks and favourites)
 * - decks
 * - favourites
 * - players (for life counter)
 */
public class CardsInfoDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cardsinfo.db";
    protected static final int DATABASE_VERSION = 8;

    public CardsInfoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CardDataSource.generateCreateTable());
        db.execSQL(DeckDataSource.generateCreateTable());
        db.execSQL(DeckDataSource.generateCreateTableJoin());
        db.execSQL(PlayerDataSource.generateCreateTable());
        db.execSQL(FavouritesDataSource.generateCreateTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3 && newVersion >= 3) {
            db.execSQL(PlayerDataSource.generateCreateTable());
            db.execSQL(FavouritesDataSource.generateCreateTable());
        }
        if (oldVersion < 4 && newVersion >= 4) {
            db.execSQL(DeckDataSource.generateCreateTable());
            db.execSQL(DeckDataSource.generateCreateTableJoin());
        }
        Set<String> columns = readColumnTable(db, CardDataSource.TABLE);
        if (!columns.contains(CardDataSource.COLUMNS.LAYOUT.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_LAYOUT);
        }
        if (!columns.contains(CardDataSource.COLUMNS.RULINGS.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_RULINGS);
        }
        if (!columns.contains(CardDataSource.COLUMNS.SET_CODE.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_SET_CODE);
        }
        if (!columns.contains(CardDataSource.COLUMNS.NUMBER.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_NUMBER);
        }
        if (!columns.contains(CardDataSource.COLUMNS.NAMES.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_NAMES);
        }
        if (!columns.contains(CardDataSource.COLUMNS.SUPER_TYPES.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_SUPER_TYPES);
        }
        if (!columns.contains(CardDataSource.COLUMNS.FLAVOR.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_FLAVOR);
        }
        if (!columns.contains(CardDataSource.COLUMNS.ARTIST.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_ARTIST);
        }
        if (!columns.contains(CardDataSource.COLUMNS.LOYALTY.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_LOYALTY);
        }
        if (!columns.contains(CardDataSource.COLUMNS.PRINTINGS.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_PRINTINGS);
        }
        if (!columns.contains(CardDataSource.COLUMNS.LEGALITIES.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_LEGALITIES);
        }
        if (!columns.contains(CardDataSource.COLUMNS.ORIGINAL_TEXT.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_ORIGINAL_TEXT);
        }
        if (!columns.contains(CardDataSource.COLUMNS.MCI_NUMBER.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_MCI_NUMBER);
        }
        if (!columns.contains(CardDataSource.COLUMNS.COLORS_IDENTITY.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_COLORS_IDENTITY);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized void clear() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + CardDataSource.TABLE + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + DeckDataSource.TABLE + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + DeckDataSource.TABLE + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + DeckDataSource.TABLE_JOIN + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + PlayerDataSource.TABLE + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + FavouritesDataSource.TABLE + "'");
    }

    Set<String> readColumnTable(SQLiteDatabase db, String table) {
        Cursor dbCursor = db.rawQuery("PRAGMA table_info(MTGCard)", null);
        Set<String> columns = new HashSet<>(dbCursor.getCount());
        if (dbCursor.moveToFirst()) {
            do {
                columns.add(dbCursor.getString(1));
            } while (dbCursor.moveToNext());
        }
        dbCursor.close();
        return columns;
    }

}
