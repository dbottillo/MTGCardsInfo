package com.dbottillo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.BuildConfig;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by danielebottillo on 02/08/2014.
 */
public abstract class CardDatabaseHelper extends SQLiteAssetHelper {

    public CardDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public abstract Cursor getSets();

    public abstract Cursor getSet(String idSet);

    public abstract Cursor searchCard(String search);


    public static CardDatabaseHelper getDatabaseHelper(Context context){
        if (BuildConfig.magic) {
            return new MTGDatabaseHelper(context);
        } else {
            return new HSDatabaseHelper(context);
        }
    }
}
