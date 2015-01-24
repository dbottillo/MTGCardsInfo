package com.dbottillo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.BuildConfig;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public abstract class CardDatabaseHelper extends SQLiteAssetHelper {

    public CardDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public abstract Cursor getSets();

    public abstract Cursor getSet(String idSet);

    public abstract Cursor searchCard(String search);

    public abstract Cursor getRandomCard();


    public static CardDatabaseHelper getDatabaseHelper(Context context) {
        return new MTGDatabaseHelper(context);
    }
}
