package com.dbottillo.mtgsearchfree.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.sqliteasset.SQLiteAssetHelper;

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

}
