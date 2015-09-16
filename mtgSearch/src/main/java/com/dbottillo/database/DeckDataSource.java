package com.dbottillo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.resources.Deck;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

public final class DeckDataSource {

    private SQLiteDatabase database;
    private CardsInfoDbHelper dbHelper;

    public DeckDataSource() {
    }

    public static final String TABLE_DECKS = "decks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_NAME_ARCHIVED = "archived";

    private String[] allColumnsDecks = {COLUMN_ID, COLUMN_NAME, COLUMN_COLOR,
            COLUMN_COLOR, COLUMN_NAME_ARCHIVED};

    public static final String TABLE_DECK_CARD = "deck_card";
    public static final String COLUMN_DECK_ID = "deck_id";
    public static final String COLUMN_CARD_ID = "card_id";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_SIDE = "side";

    private String[] allColumnsDeckCard = {COLUMN_ID, COLUMN_DECK_ID, COLUMN_CARD_ID,
            COLUMN_QUANTITY, COLUMN_SIDE};

    protected static final String CREATE_DECKS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_DECKS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_COLOR + " text, "
            + COLUMN_NAME_ARCHIVED + " integer);";

    protected static final String CREATE_DECK_CARD_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_DECK_CARD + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_DECK_ID + " integer not null, "
            + COLUMN_CARD_ID + " integer not null, "
            + COLUMN_QUANTITY + " integer not null, "
            + COLUMN_SIDE + " integer not null);";


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

    public void addCardToDeck(Deck deck, MTGCard card, int quantity, boolean side) {
        long id = CardDataSource.saveCard(database, card);
        ContentValues values = new ContentValues();
        values.put(COLUMN_CARD_ID, id);
        values.put(COLUMN_DECK_ID, deck.getId());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_SIDE, side ? 1 : 0);
        database.insert(TABLE_DECK_CARD, null, values);
    }

    public ArrayList<MTGCard> getCards(Deck deck) {
        ArrayList<MTGCard> cards = new ArrayList<>();
        Cursor cursor = database.rawQuery("select P.* from MTGCard P inner join deck_card H on (H.card_id = P._id and H.deck_id = ?)", new String[]{deck.getId() + ""});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MTGCard card = MTGCard.createCardFromCursor(cursor);
            cards.add(card);
            cursor.moveToNext();
        }
        cursor.close();
        return cards;
    }
}
