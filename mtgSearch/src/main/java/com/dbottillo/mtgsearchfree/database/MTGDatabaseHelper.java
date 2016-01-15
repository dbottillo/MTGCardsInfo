package com.dbottillo.mtgsearchfree.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.resources.MTGSet;
import com.dbottillo.mtgsearchfree.search.SearchParams;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * Helper for access the card database, only on read mode
 * the database is created from {@link CreateDatabaseHelper}
 * and then copied to database folder from the library {@link SQLiteAssetHelper}
 */
public class MTGDatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "mtgsearch.db";

    public MTGDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MTGDatabaseHelper(Context context) {
        this(context, DATABASE_NAME, null, BuildConfig.DATABASE_VERSION);
        setForcedUpgrade();
    }

    public ArrayList<MTGSet> getSets() {
        return SetDataSource.getSets(getReadableDatabase());
    }

    public Cursor getSet(String idSet) {
        SQLiteDatabase db = getReadableDatabase();
        return CardDataSource.getSet(db, idSet);
    }

    public Cursor searchCards(SearchParams searchParams) {
        SQLiteDatabase db = getReadableDatabase();
        return CardDataSource.searchCards(db, searchParams);
    }

    public Cursor getRandomCard(int number) {
        SQLiteDatabase db = getReadableDatabase();
        return CardDataSource.getRandomCard(db, number);
    }

}
