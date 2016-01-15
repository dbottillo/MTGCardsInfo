package com.dbottillo.mtgsearchfree.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dbottillo.mtgsearchfree.helper.LOG;

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
        LOG.e("on create");
        db.execSQL(SetDataSource.TABLE);
        db.execSQL(CardContract.SQL_CREATE_CARDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SetDataSource.TABLE);
        db.execSQL(CardContract.SQL_DELETE_CARDS_TABLE);
        onCreate(db);
    }
}
