package com.dbottillo.mtgsearchfree.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.List;

public class DeckDataSource {

    public final static String TABLE = "decks";
    final static String TABLE_JOIN = "deck_card";

    private final SQLiteDatabase database;
    private final CardDataSource cardDataSource;
    private final MTGCardDataSource mtgCardDataSource;

    public DeckDataSource(SQLiteDatabase database, CardDataSource cardDataSource, MTGCardDataSource mtgCardDataSource) {
        this.database = database;
        this.cardDataSource = cardDataSource;
        this.mtgCardDataSource = mtgCardDataSource;
    }

    public static String generateCreateTable() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        for (COLUMNS column : COLUMNS.values()) {
            builder.append(column.name).append(' ').append(column.type);
            if (column != COLUMNS.ARCHIVED) {
                builder.append(',');
            }
        }
        builder.append(')');
        return builder.toString();
    }

    public static String generateCreateTableJoin() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE_JOIN).append(" (");
        for (COLUMNSJOIN column : COLUMNSJOIN.values()) {
            builder.append(column.name).append(' ').append(column.type);
            if (column != COLUMNSJOIN.SIDE) {
                builder.append(',');
            }
        }
        builder.append(')');
        return builder.toString();
    }

    public long addDeck(String name) {
        ContentValues values = new ContentValues();
        values.put(COLUMNS.NAME.getName(), name);
        values.put(COLUMNS.ARCHIVED.getName(), 0);
        return database.insert(TABLE, null, values);
    }

    public long addDeck(CardsBucket bucket) {
        long deckId = addDeck(bucket.getKey());
        if (bucket.getCards().isEmpty()) {
            return deckId;
        }
        for (MTGCard card : bucket.getCards()) {
            MTGCard realCard = mtgCardDataSource.searchCard(card.getName());
            if (realCard != null) {
                realCard.setSideboard(card.isSideboard());
                addCardToDeck(deckId, realCard, card.getQuantity());
            }
        }
        return deckId;
    }

    public void addCardToDeck(long deckId, MTGCard card, int quantity) {
        int sid = card.isSideboard() ? 1 : 0;
        int currentCard = 0;
        String query = "select H.*,P.* from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ? and P.multiVerseId = ? and H.side == ?)";
        LOG.query(query, deckId + "", card.getMultiVerseId() + "", sid + "");
        Cursor cardsCursor = database.rawQuery(query, new String[]{deckId + "", card.getMultiVerseId() + "", sid + ""});
        if (cardsCursor.getCount() > 0) {
            LOG.d("card already in the database");
            cardsCursor.moveToFirst();
            currentCard = cardsCursor.getInt(cardsCursor.getColumnIndex(COLUMNSJOIN.QUANTITY.getName()));
        }
        if (currentCard + quantity < 0) {
            LOG.d("the quantity is negative and is bigger than the current quantity so needs to be removed");
            cardsCursor.close();
            removeCardFromDeck(deckId, card);
            return;
        }
        if (currentCard > 0) {
            // there is already some cards there! just need to add the quantity
            LOG.d("just need to update the quantity");
            updateQuantity(deckId,  currentCard + quantity, card.getMultiVerseId(), sid);
            cardsCursor.close();
            return;
        }
        cardsCursor.close();
        addCardToDeckWithoutCheck(deckId, card, quantity);
    }

    public void addCardToDeckWithoutCheck(long deckId, MTGCard card, int quantity) {
        String query = "select * from MTGCard where multiVerseId=?";
        LOG.query(query, card.getMultiVerseId() + "");
        Cursor current = database.rawQuery(query, new String[]{card.getMultiVerseId() + ""});
        if (current.getCount() > 0) {
            // card already added
            if (current.getCount() > 1) {
                // there is a duplicate
                current.moveToFirst();
                current.moveToNext();
                while (!current.isAfterLast()) {
                    String query2 = "delete from MTGCard where _id=?";
                    LOG.query(query2, current.getString(0));
                    database.rawQuery(query2, new String[]{current.getString(0)}).moveToFirst();
                    current.moveToNext();
                }
            }
        } else {
            // need to add the card
            long cardId = cardDataSource.saveCard(card);
        }
        current.close();
        ContentValues values = new ContentValues();
        values.put(COLUMNSJOIN.CARD_ID.getName(), card.getMultiVerseId());
        values.put(COLUMNSJOIN.DECK_ID.getName(), deckId);
        values.put(COLUMNSJOIN.QUANTITY.getName(), quantity);
        values.put(COLUMNSJOIN.SIDE.getName(), card.isSideboard() ? 1 : 0);
        database.insert(TABLE_JOIN, null, values);
    }

    public Deck getDeck(long deckId){
        String query = "select * from " + TABLE + " where rowid =?";
        Cursor cursor = database.rawQuery(query, new String[]{deckId + ""});
        LOG.query(query);
        cursor.moveToFirst();
        Deck deck = fromCursor(cursor);
        cursor.close();
        return deck;
    }

    public List<Deck> getDecks() {
        ArrayList<Deck> decks = new ArrayList<>();
        String query = "Select * from decks";
        LOG.query(query);
        Cursor deckCursor = database.rawQuery(query, null);
        deckCursor.moveToFirst();
        while (!deckCursor.isAfterLast()) {
            Deck newDeck = fromCursor(deckCursor);
            decks.add(newDeck);
            deckCursor.moveToNext();
        }
        deckCursor.close();
        setQuantityOfCards(decks, false);   // standard cards
        setQuantityOfCards(decks, true);    // sideboard cards
        return decks;
    }

    private void setQuantityOfCards(ArrayList<Deck> decks, boolean side) {
        // need to loadSet cards now
        //select SUM(quantity),* from deck_card DC left join decks D on (D._id = DC.deck_id) where side= 0 group by DC.deck_id
        //Cursor cursor = database.rawQuery("Select * from decks D left join deck_card DC on (D._id = DC.deck_id) where DC.side=0", null);
        //Cursor cursor = database.rawQuery("select SUM(quantity),D._id from deck_card DC left join decks D on (D._id = DC.deck_id) where side= 0 group by DC.deck_id", null);
        String query = "select SUM(quantity),D._id from deck_card DC left join decks D on (D._id = DC.deck_id) where side=" + (side ? 1 : 0) + " group by DC.deck_id";
        LOG.query(query);
        Cursor cursor = database.rawQuery(query, null);
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

    public List<MTGCard> getCards(Deck deck) {
        return getCards(deck.getId());
    }

    public List<MTGCard> getCards(long deckId) {
        ArrayList<MTGCard> cards = new ArrayList<>();
        String query = "select H.*,P.* from MTGCard P inner join deck_card H on (H.card_id = P.multiVerseId and H.deck_id = ?)";
        LOG.query(query, deckId + "");
        Cursor cursor = database.rawQuery(query, new String[]{deckId + ""});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MTGCard card = cardDataSource.fromCursor(cursor);
            int quantity = cursor.getInt(cursor.getColumnIndex(COLUMNSJOIN.QUANTITY.getName()));
            card.setQuantity(quantity);
            int sideboard = cursor.getInt(cursor.getColumnIndex(COLUMNSJOIN.SIDE.getName()));
            card.setSideboard(sideboard == 1);
            cards.add(card);
            cursor.moveToNext();
        }
        cursor.close();
        return cards;
    }

    public void removeCardFromDeck(long deckId, MTGCard card) {
        int sid = card.isSideboard() ? 1 : 0;
        String[] args = new String[]{deckId + "", card.getMultiVerseId() + "", sid + ""};
        String query = "DELETE FROM deck_card where deck_id=? and card_id=? and side =?";
        LOG.query(query, args);
        Cursor cursor = database.rawQuery(query, args);
        cursor.moveToFirst();
        cursor.close();
    }

    public void deleteDeck(Deck deck) {
        String[] args = new String[]{deck.getId() + ""};
        String query = "DELETE FROM deck_card where deck_id=? ";
        LOG.query(query, args);
        Cursor cursor = database.rawQuery(query, args);
        cursor.moveToFirst();
        cursor.close();
        String query2 = "DELETE FROM decks where _id=? ";
        Cursor cursor2 = database.rawQuery(query2, args);
        cursor2.moveToFirst();
        cursor2.close();
    }

    public void deleteAllDecks(SQLiteDatabase db) {
        String query = "DELETE FROM deck_card";
        LOG.query(query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        cursor.close();
        String query2 = "DELETE FROM decks";
        LOG.query(query2);
        Cursor cursor2 = db.rawQuery(query2, null);
        cursor2.moveToFirst();
        cursor2.close();
    }

    public void moveCardToSideBoard(long deckId, MTGCard card, int quantity) {
        moveCardInDeck(deckId, card, quantity, true);
    }

    public void moveCardFromSideBoard(long deckId, MTGCard card, int quantity) {
        moveCardInDeck(deckId, card, quantity, false);
    }

    private void moveCardInDeck(long deckId, MTGCard card, int quantity, boolean fromDeckToSide){
        boolean removeCard = false;
        int before = fromDeckToSide ? 0 : 1;
        int after = fromDeckToSide ? 1 : 0;

        Cursor cursor = runQuery("select quantity from deck_card where deck_id=? and card_id=? and side = ?",
                String.valueOf(deckId), String.valueOf(card.getMultiVerseId()), String.valueOf(before));
        if (cursor.moveToFirst()){
            if (cursor.getInt(0) - quantity <= 0){
                removeCard = true;
            } else {
                updateQuantity(deckId, cursor.getInt(0) - quantity, card.getMultiVerseId(), before);
            }
        }
        cursor.close();

        Cursor cursorSideboard = runQuery("select quantity from deck_card where deck_id=? and card_id=? and side = ?",
                String.valueOf(deckId), String.valueOf(card.getMultiVerseId()), String.valueOf(after));
        if (cursorSideboard.moveToFirst()){
            updateQuantity(deckId, cursorSideboard.getInt(0) + quantity, card.getMultiVerseId(), after);
        } else {
            // card wasn't in the deck
            card.setSideboard(before == 0);
            addCardToDeckWithoutCheck(deckId, card, quantity);
        }
        cursorSideboard.close();

        if (removeCard){
            card.setSideboard(before == 1);
            removeCardFromDeck(deckId, card);
        }
    }

    public void updateQuantity(long deckId, int quantity, int multiverseId, int sid){
        ContentValues values = new ContentValues();
        values.put(COLUMNSJOIN.QUANTITY.getName(), quantity);
        String query = "UPDATE " +TABLE_JOIN+" SET quantity=? WHERE "+COLUMNSJOIN.DECK_ID.getName() + " = ? and "
                + COLUMNSJOIN.CARD_ID.getName() + " = ? and " + COLUMNSJOIN.SIDE.getName() + " = ?";
        String[] args = new String[]{String.valueOf(quantity), deckId + "", multiverseId + "", sid + ""};
        Cursor cursor = runQuery(query, args);
        cursor.moveToFirst();
        cursor.close();
    }

    public int renameDeck(long deckId, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMNS.NAME.getName(), name);
        return database.update(TABLE, contentValues, "_id=" + deckId, null);
    }

    public Deck fromCursor(Cursor cursor) {
        Deck deck = new Deck(cursor.getLong(cursor.getColumnIndex("_id")));
        deck.setName(cursor.getString(cursor.getColumnIndex(COLUMNS.NAME.getName())));
        deck.setArchived(cursor.getInt(cursor.getColumnIndex(COLUMNS.ARCHIVED.getName())) == 1);
        return deck;
    }

    private Cursor runQuery(String query, String... args){
        Cursor cursor = database.rawQuery(query, args);
        LOG.query(query, args);
        return cursor;
    }

    private void runQueryAndClose(String query, String... args){
        runQuery(query, args).close();
    }


    private enum COLUMNS {
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

    private enum COLUMNSJOIN {
        DECK_ID("deck_id", "INT not null"),
        CARD_ID("card_id", "INT not null"),
        QUANTITY("quantity", "INT not null"),
        SIDE("side", "INT");

        private String name;
        private String type;

        COLUMNSJOIN(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }
    }
}
