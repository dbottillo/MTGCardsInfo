package com.dbottillo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.BuildConfig;
import com.dbottillo.database.CardContract.CardEntry;
import com.dbottillo.database.SetContract.SetEntry;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class CardsDatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "mtgsearch.db";
    public static final int LIMIT = 400;

    public CardsDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public CardsDatabaseHelper(Context context) {
        this(context, DATABASE_NAME, null, BuildConfig.DATABASE_VERSION);
        setForcedUpgrade();
    }

    public Cursor getSets() {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + SetEntry.TABLE_NAME;

        return db.rawQuery(query, null);
    }

    public Cursor getSet(String idSet) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + CardEntry.TABLE_NAME + " WHERE " + CardEntry.COLUMN_NAME_SET_ID + " = ?";

        //Log.d("MTG", "query: "+query+" with id: "+idSet);

        return db.rawQuery(query, new String[]{idSet});
    }

    public Cursor searchCard(String search) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + CardEntry.TABLE_NAME + " WHERE " + CardEntry.COLUMN_NAME_NAME + " LIKE ? LIMIT " + LIMIT;

        //Log.d("MTG", "query: "+query+" with id: "+search);

        return db.rawQuery(query, new String[]{"%" + search + "%"});
    }

    public Cursor getRandomCard(int number) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + CardEntry.TABLE_NAME + " ORDER BY RANDOM() LIMIT "+number;

        //Log.e("MTG", "query: " + query);

        return db.rawQuery(query, null);
    }

}
