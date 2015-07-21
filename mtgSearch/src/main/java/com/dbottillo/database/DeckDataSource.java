package com.dbottillo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.dbottillo.resources.Deck;

import java.util.ArrayList;

public final class DeckDataSource {

    private SQLiteDatabase database;
    private CardsInfoDbHelper dbHelper;

    public DeckDataSource() {
    }

    public static abstract class DeckEntry implements BaseColumns {
        public static final String TABLE_NAME = "decks";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SIDE = "side";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_ARCHIVED = "archived";
    }

   /* public static abstract class DeckCardEntry implements BaseColumns {
        public static final String TABLE_NAME = "deck_card";
        public static final String COLUMN_NAME_DECK_ID = "deck_id";
        public static final String COLUMN_NAME_CARD_ID = "card_id";
        public static final String COLUMN_SIDE = "side";
    }*/

    public static final String TABLE_DECKS = "decks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_NAME_ARCHIVED = "archived";

    private String[] allColumnsDecks = {COLUMN_ID, COLUMN_NAME, COLUMN_COLOR,
            COLUMN_COLOR, COLUMN_NAME_ARCHIVED};

    /*public static final String COLUMN_RECIPIENT_TYPES = "recipientTypes";
    public static final String COLUMN_PAYMENT_REFERENCE_MAX_LENGTH = "paymentReferenceMaxLength";
    public static final String COLUMN_RECIPIENT_EMAIL_REQUIRED = "recipientEmailRequired";
    public static final String COLUMN_PAYMENT_REFERENCE_ALLOWED = "paymentReferenceAllowed";
    public static final String COLUMN_RECIPIENT_BIC_REQUIRED = "recipientBicRequired";*/


    protected static final String CREATE_DECKS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_DECKS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_COLOR + " text, "
            + COLUMN_NAME_ARCHIVED + " integer);";

    public DeckDataSource(Context context) {
        dbHelper = CardsInfoDbHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void addDeck(String name) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        //values.put(COLUMN_COLOR, currency.getCode());
        values.put(COLUMN_NAME_ARCHIVED, 0);
        database.insert(TABLE_DECKS, null, values);
    }

    public ArrayList<Deck> getDecks() {
        ArrayList<Deck> decks = new ArrayList<>();

        Cursor cursor = database.query(TABLE_DECKS,
                allColumnsDecks, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Deck deck = cursorToDeck(cursor);
            decks.add(deck);
            cursor.moveToNext();
        }
        cursor.close();
        return decks;
    }


    private Deck cursorToDeck(Cursor cursor) {
        Deck deck = new Deck(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        deck.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        return deck;
    }

}
