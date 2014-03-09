package com.dbottillo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dbottillo.database.SetContract.*;
import com.dbottillo.database.CardContract.*;

/**
 * Created by danielebottillo on 04/03/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MTGCardsInfo.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_SET =
            "CREATE TABLE " + SetEntry.TABLE_NAME + " (" +
                    SetEntry._ID + " INTEGER PRIMARY KEY," +
                    SetEntry.COLUMN_NAME_CODE + TEXT_TYPE + COMMA_SEP +
                    SetEntry.COLUMN_NAME_NAME + TEXT_TYPE +" )";

    private static final String SQL_DELETE_SET =
            "DROP TABLE IF EXISTS " + SetEntry.TABLE_NAME;

    private static final String SQL_CREATE_CARD =
            "CREATE TABLE " + CardEntry.TABLE_NAME + " (" +
                    CardEntry._ID + " INTEGER PRIMARY KEY," +
                    CardEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_TYPES + TEXT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_SUBTYPES + TEXT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_COLORS + TEXT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_CMC + INT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_RARITY + TEXT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_POWER + TEXT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_TOUGHNESS + TEXT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_MANACOST + TEXT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_MULTICOLOR + INT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_LAND + INT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_ARTIFACT + INT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_MULTIVERSEID + INT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_SET_ID + INT_TYPE + COMMA_SEP +
                    CardEntry.COLUMN_NAME_SET_NAME + TEXT_TYPE +" )";


    private static final String SQL_DELETE_CARD =
            "DROP TABLE IF EXISTS " + CardEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("MTG", "on create database");
        db.execSQL(SQL_CREATE_SET);
        db.execSQL(SQL_CREATE_CARD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("MTG", "on upgrade database");
        db.execSQL(SQL_DELETE_SET);
        db.execSQL(SQL_DELETE_CARD);
        onCreate(db);
    }
}
