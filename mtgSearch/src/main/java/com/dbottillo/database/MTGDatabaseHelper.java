package com.dbottillo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.dbottillo.database.SetContract.*;
import com.dbottillo.database.CardContract.*;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by danielebottillo on 05/03/2014.
 */
public class MTGDatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "mtgsearch.db";
    public static final int LIMIT = 400;
    private static final int DATABASE_VERSION = 1;

    public MTGDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getSets() {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + SetEntry.TABLE_NAME;

        return db.rawQuery(query, null);
    }

    public Cursor getSet(String idSet) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String query = "SELECT * FROM " + CardEntry.TABLE_NAME + " WHERE "+CardEntry.COLUMN_NAME_SET_ID+" = ?";

        //Log.d("MTG", "query: "+query+" with id: "+idSet);

        return db.rawQuery(query, new String[] {idSet});
    }

    public Cursor searchCard(String search){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String query = "SELECT * FROM " + CardEntry.TABLE_NAME + " WHERE "+CardEntry.COLUMN_NAME_NAME+" LIKE ? LIMIT "+LIMIT;

        //Log.d("MTG", "query: "+query+" with id: "+search);

        return db.rawQuery(query, new String[] {"%"+search+"%"});
    }

}
