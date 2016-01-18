package com.dbottillo.mtgsearchfree.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.resources.Deck;
import com.dbottillo.mtgsearchfree.resources.MTGCard;

import java.util.ArrayList;

public final class DeckDataSource {

    public static final String TABLE = "decks";
    public static final String TABLE_JOIN = "deck_card";

    public enum COLUMNS {
        NAME("name", "TEXT not null"),
        COLOR("color", "TEXT"),
        ARCHIVED("archived", "INT");

        private String name;
        private String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }
    }

    public enum COLUMNS_JOIN {
        DECK_ID("deck_id", "INT not null"),
        CARD_ID("card_id", "INT not null"),
        QUANTITY("quantity", "INT not null"),
        SIDE("side", "INT");

        private String name;
        private String type;

        COLUMNS_JOIN(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }
    }

    public static String generateCreateTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, ";
        for (COLUMNS column : COLUMNS.values()) {
            query += column.name + " " + column.type + ",";
        }
        return query.substring(0, query.length() - 1) + ")";
    }

    public static String generateCreateTableJoin() {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_JOIN + " (";
        for (COLUMNS_JOIN column : COLUMNS_JOIN.values()) {
            query += column.name + " " + column.type + ",";
        }
        return query.substring(0, query.length() - 1) + ")";
    }

    public static long addDeck(SQLiteDatabase db, String name) {
        ContentValues values = new ContentValues();
        values.put(COLUMNS.NAME.getName(), name);
        values.put(COLUMNS.ARCHIVED.getName(), 0);
        return db.insert(TABLE, null, values);
    }

    public static void addCardToDeck(SQLiteDatabase db, long deckId, MTGCard card, int quantity, boolean side) {
        int sid = side ? 1 : 0;
        int currentCard = 0;
        Cursor cardsCursor = db.rawQuery("select H.*,P.* from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ? and P.multiVerseId = ? and H.side == ?)", new String[]{deckId + "", card.getMultiVerseId() + "", sid + ""});
        if (cardsCursor.getCount() > 0) {
            cardsCursor.moveToFirst();
            currentCard = cardsCursor.getInt(cardsCursor.getColumnIndex(COLUMNS_JOIN.QUANTITY.getName()));
        }
        if (currentCard + quantity < 0) {
            removeCardFromDeck(db, deckId, card, side);
            return;
        }
        if (currentCard > 0) {
            // there is already some cards there! just need to add the quantity
            ContentValues values = new ContentValues();
            values.put(COLUMNS_JOIN.QUANTITY.getName(), currentCard + quantity);
            db.update(TABLE_JOIN, values, COLUMNS_JOIN.DECK_ID.getName() + " = ? and " + COLUMNS_JOIN.CARD_ID.getName() + " = ? and " + COLUMNS_JOIN.SIDE.getName() + " = ?", new String[]{deckId + "", card.getMultiVerseId() + "", sid + ""});
            cardsCursor.close();
            return;
        }
        addCardToDeckWithoutCheck(db, deckId, card, quantity, side);
    }

    public static void addCardToDeckWithoutCheck(SQLiteDatabase db, long deckId, MTGCard card, int quantity, boolean side) {
        Cursor current = db.rawQuery("select * from MTGCard where multiVerseId=?", new String[]{card.getMultiVerseId() + ""});
        if (current.getCount() > 0) {
            // card already added
            if (current.getCount() > 1) {
                // there is a duplicate
                current.moveToFirst();
                current.moveToNext();
                while (!current.isAfterLast()) {
                    //LOG.e("deletingL "+current.getString(0));
                    db.rawQuery("delete from MTGCard where _id=?", new String[]{current.getString(0)}).moveToFirst();
                    current.moveToNext();
                }
            }
        } else {
            // need to add the card
            CardDataSource.saveCard(db, card);
        }
        current.close();
        ContentValues values = new ContentValues();
        values.put(COLUMNS_JOIN.CARD_ID.getName(), card.getMultiVerseId());
        values.put(COLUMNS_JOIN.DECK_ID.getName(), deckId);
        values.put(COLUMNS_JOIN.QUANTITY.getName(), quantity);
        values.put(COLUMNS_JOIN.SIDE.getName(), side ? 1 : 0);
        db.insert(TABLE_JOIN, null, values);
    }

    public static ArrayList<Deck> getDecks(SQLiteDatabase db) {
        ArrayList<Deck> decks = new ArrayList<>();

        Cursor deckCursor = db.rawQuery("Select * from decks", null);
        deckCursor.moveToFirst();
        while (!deckCursor.isAfterLast()) {
            Deck newDeck = fromCursor(deckCursor);
            decks.add(newDeck);
            deckCursor.moveToNext();
        }
        deckCursor.close();
        setQuantityOfCards(db, decks, false);   // standard cards
        setQuantityOfCards(db, decks, true);    // sideboard cards
        return decks;
    }

    private static void setQuantityOfCards(SQLiteDatabase db, ArrayList<Deck> decks, boolean side) {
        // need to load cards now
        //select SUM(quantity),* from deck_card DC left join decks D on (D._id = DC.deck_id) where side= 0 group by DC.deck_id
        //Cursor cursor = database.rawQuery("Select * from decks D left join deck_card DC on (D._id = DC.deck_id) where DC.side=0", null);
        //Cursor cursor = db.rawQuery("select SUM(quantity),D._id from deck_card DC left join decks D on (D._id = DC.deck_id) where side= 0 group by DC.deck_id", null);
        Cursor cursor = db.rawQuery("select SUM(quantity),D._id from deck_card DC left join decks D on (D._id = DC.deck_id) where side=" + (side ? 1 : 0) + " group by DC.deck_id", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            for (Deck deck : decks) {
                if (deck.getId() == cursor.getInt(1)) {
                    if (side) {
                        deck.setSizeOfSideboard(cursor.getInt(0));
                    } else {
                        deck.setNumberOfCards(cursor.getInt(0));
                    }
                    break;
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

    public static ArrayList<MTGCard> getCards(SQLiteDatabase db, Deck deck) {
        ArrayList<MTGCard> cards = new ArrayList<>();
        Cursor cursor = db.rawQuery("select H.*,P.* from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ?)", new String[]{deck.getId() + ""});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MTGCard card = CardDataSource.fromCursor(cursor);
            int quantity = cursor.getInt(cursor.getColumnIndex(COLUMNS_JOIN.QUANTITY.getName()));
            card.setQuantity(quantity);
            int sideboard = cursor.getInt(cursor.getColumnIndex(COLUMNS_JOIN.SIDE.getName()));
            card.setSideboard(sideboard == 1);
            cards.add(card);
            cursor.moveToNext();
        }
        cursor.close();
        return cards;
    }

    public static void removeCardFromDeck(SQLiteDatabase db, long deckId, MTGCard card, boolean sideboard) {
        int sid = sideboard ? 1 : 0;
        String[] args = new String[]{deckId + "", card.getMultiVerseId() + "", sid + ""};
        db.rawQuery("DELETE FROM deck_card where deck_id=? and card_id=? and side =?", args).moveToFirst();
    }

    public static void deleteDeck(SQLiteDatabase db, Deck deck) {
        String[] args = new String[]{deck.getId() + ""};
        db.rawQuery("DELETE FROM deck_card where deck_id=? ", args).moveToFirst();
        db.rawQuery("DELETE FROM decks where _id=? ", args).moveToFirst();
    }

    public static void deleteAllDecks(SQLiteDatabase db) {
        db.rawQuery("DELETE FROM deck_card", null).moveToFirst();
        db.rawQuery("DELETE FROM decks", null).moveToFirst();
    }

    protected static Deck fromCursor(Cursor cursor) {
        Deck deck = new Deck(cursor.getLong(cursor.getColumnIndex("_id")));
        deck.setName(cursor.getString(cursor.getColumnIndex(COLUMNS.NAME.getName())));
        deck.setArchived(cursor.getInt(cursor.getColumnIndex(COLUMNS.ARCHIVED.getName())) == 1);
        return deck;
    }
}
