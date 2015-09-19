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

    public long addDeck(String name) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        //values.put(COLUMN_COLOR, currency.getCode());
        values.put(COLUMN_NAME_ARCHIVED, 0);
        return database.insert(TABLE_DECKS, null, values);
    }

    public ArrayList<Deck> getDecks() {
        ArrayList<Deck> decks = new ArrayList<>();

        Cursor deckCursor = database.rawQuery("Select * from decks", null);
        deckCursor.moveToFirst();
        while (!deckCursor.isAfterLast()) {
            Deck newDeck = cursorToDeck(deckCursor);
            decks.add(newDeck);
            deckCursor.moveToNext();
        }
        deckCursor.close();

        // need to load cards now
        //select SUM(quantity),* from deck_card DC left join decks D on (D._id = DC.deck_id) where side= 0 group by DC.deck_id
        //Cursor cursor = database.rawQuery("Select * from decks D left join deck_card DC on (D._id = DC.deck_id) where DC.side=0", null);
        Cursor cursor = database.rawQuery("select SUM(quantity),D._id from deck_card DC left join decks D on (D._id = DC.deck_id) where side= 0 group by DC.deck_id", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            for (Deck deck : decks) {
                if (deck.getId() == cursor.getInt(1)) {
                    //deck.addNumberOfCards(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)));
                    deck.setNumberOfCards(cursor.getInt(0));
                    break;
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        return decks;
    }


    private Deck cursorToDeck(Cursor cursor) {
        Deck deck = new Deck(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
        deck.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        return deck;
    }

    public void addCardToDeck(String newDeck, MTGCard card, int quantity, boolean side) {
        long deckId = addDeck(newDeck);
        addCardToDeck(deckId, card, quantity, side);
    }

    public void addCardToDeck(long deckId, MTGCard card, int quantity, boolean side) {
        if (card.getQuantity() == 1 && quantity < 0) {
            removeCardFromDeck(deckId, card, side);
            return;
        }
        int sid = side ? 1 : 0;
        Cursor cardsCursor = database.rawQuery("select H.*,P.* from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ? and P.multiVerseId = ? and H.side == ?)", new String[]{deckId + "", card.getMultiVerseId() + "", sid + ""});
        if (cardsCursor.getCount() > 0) {
            // there is already some cards there! just need to add the quantity
            cardsCursor.moveToFirst();
            int currentQuantity = cardsCursor.getInt(cardsCursor.getColumnIndex(COLUMN_QUANTITY));
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, currentQuantity + quantity);
            database.update(TABLE_DECK_CARD, values, COLUMN_DECK_ID + " = ? and " + COLUMN_CARD_ID + " = ? and " + COLUMN_SIDE + " = ?", new String[]{deckId + "", card.getMultiVerseId() + "", sid + ""});
            cardsCursor.close();
            return;
        }
        addCardToDeckWithoutCheck(deckId, card, quantity, side);
    }

    public void addCardToDeckWithoutCheck(long deckId, MTGCard card, int quantity, boolean side){
        CardDataSource.saveCard(database, card);
        ContentValues values = new ContentValues();
        values.put(COLUMN_CARD_ID, card.getMultiVerseId());
        values.put(COLUMN_DECK_ID, deckId);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_SIDE, side ? 1 : 0);
        database.insert(TABLE_DECK_CARD, null, values);
    }

    public ArrayList<MTGCard> getCards(Deck deck) {
        ArrayList<MTGCard> cards = new ArrayList<>();
        Cursor cursor = database.rawQuery("select H.*,P.* from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ?)", new String[]{deck.getId() + ""});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MTGCard card = MTGCard.createCardFromCursor(cursor);
            int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
            card.setQuantity(quantity);
            int sideboard = cursor.getInt(cursor.getColumnIndex(COLUMN_SIDE));
            card.setSideboard(sideboard == 1);
            cards.add(card);
            cursor.moveToNext();
        }
        cursor.close();
        return cards;
    }

    public void removeCardFromDeck(long deckId, MTGCard card, boolean sideboard) {
        int sid = sideboard ? 1 : 0;
        String[] args = new String[]{deckId + "", card.getMultiVerseId() + "", sid + ""};
        database.rawQuery("DELETE FROM deck_card where deck_id=? and card_id=? and side =?", args).moveToFirst();
    }

    public void deleteDeck(Deck deck) {
        String[] args = new String[]{deck.getId() + ""};
        database.rawQuery("DELETE FROM deck_card where deck_id=? ", args).moveToFirst();
        database.rawQuery("DELETE FROM decks where _id=? ", args).moveToFirst();
    }


    public void deleteAllDecks() {
        database.rawQuery("DELETE FROM deck_card", null).moveToFirst();
        database.rawQuery("DELETE FROM decks", null).moveToFirst();
    }
}
