package com.dbottillo.mtgsearchfree.model.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.VisibleForTesting;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper for access the use database that contains:
 * - cards (that are in decks and favourites)
 * - decks
 * - favourites
 * - players (for life counter)
 */
public final class CardsInfoDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cardsinfo.db";
    protected static final int DATABASE_VERSION = 6;

    private static CardsInfoDbHelper instance;

    public static synchronized CardsInfoDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CardsInfoDbHelper(context);
        }
        return instance;
    }

    @VisibleForTesting
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

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized void clear() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CardDataSource.TABLE, null, null);
        db.delete(DeckDataSource.TABLE, null, null);
        db.delete(DeckDataSource.TABLE_JOIN, null, null);
        db.delete(PlayerDataSource.TABLE, null, null);
        db.delete(FavouritesDataSource.TABLE, null, null);
    }

    public Set<String> readColumnTable(SQLiteDatabase db, String table) {
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
