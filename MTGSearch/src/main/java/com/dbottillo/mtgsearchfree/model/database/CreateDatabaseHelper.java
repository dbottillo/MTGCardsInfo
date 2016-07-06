package com.dbottillo.mtgsearchfree.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper for create the database from the json in debug mode
 */
public class CreateDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MTGCardsInfo.db";


    public CreateDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SetDataSource.generateCreateTable());
        db.execSQL(CardDataSource.generateCreateTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete(SetDataSource.TABLE, null, null);
        db.delete(CardDataSource.TABLE, null, null);
        onCreate(db);
    }
}
