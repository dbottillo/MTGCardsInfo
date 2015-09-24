package com.dbottillo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dbottillo.helper.LOG;

public class CreateDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MTGCardsInfo.db";


    public CreateDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LOG.e("on create");
        db.execSQL(SetContract.SQL_CREATE_SET_TABLE);
        db.execSQL(CardContract.SQL_CREATE_CARDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SetContract.SQL_DELETE_SET_TABLE);
        db.execSQL(CardContract.SQL_DELETE_CARDS_TABLE);
        onCreate(db);
    }
}
