package com.dbottillo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class CardsInfoDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cardsinfo.db";
    private static final int DATABASE_VERSION = 3;

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
        db.execSQL(PlayerDataSource.CREATE_PLAYERS_TABLE);
        db.execSQL(FavouritesDataSource.CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL(CardContract.SQL_ADD_COLUMN_RULINGS);
        }
        if (oldVersion < 3 && newVersion == 3){
            db.execSQL(PlayerDataSource.CREATE_PLAYERS_TABLE);
            db.execSQL(FavouritesDataSource.CREATE_FAVOURITES_TABLE);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
