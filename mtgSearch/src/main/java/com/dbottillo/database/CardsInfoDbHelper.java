package com.dbottillo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CardsInfoDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cardsinfo.db";
    private static final int DATABASE_VERSION = 1;

    private static CardsInfoDbHelper instance;

    public static synchronized CardsInfoDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CardsInfoDbHelper(context);
        }
        return instance;
    }

    private CardsInfoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CardContract.SQL_CREATE_CARDS_TABLE);
        db.execSQL(DeckDataSource.CREATE_DECKS_TABLE);
        db.execSQL(DeckDataSource.CREATE_DECK_CARD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
