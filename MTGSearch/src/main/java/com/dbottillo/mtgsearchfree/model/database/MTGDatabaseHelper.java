package com.dbottillo.mtgsearchfree.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.sqliteasset.SQLiteAssetHelper;

import java.util.List;
import java.util.List;

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

    private static MTGDatabaseHelper instance;

    @Deprecated
    public static synchronized MTGDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MTGDatabaseHelper(context);
        }
        return instance;
    }

    public List<MTGSet> getSets() {
        SQLiteDatabase db = getReadableDatabase();
        return SetDataSource.getSets(db);
    }

    public List<MTGCard> getSet(MTGSet set) {
        SQLiteDatabase db = getReadableDatabase();
        return MTGCardDataSource.getSet(db, set);
    }

    public List<MTGCard> searchCards(SearchParams searchParams) {
        SQLiteDatabase db = getReadableDatabase();
        return MTGCardDataSource.searchCards(db, searchParams);
    }

    public List<MTGCard> getRandomCard(int number) {
        SQLiteDatabase db = getReadableDatabase();
        return MTGCardDataSource.getRandomCard(db, number);
    }

}
