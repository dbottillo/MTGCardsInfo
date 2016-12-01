package com.dbottillo.mtgsearchfree.model.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.VisibleForTesting;

import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper for access the use database that contains:
 * - cards (that are in decks and favourites)
 * - decks
 * - favourites
 * - players (for life counter)
 */
public class CardsInfoDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cardsinfo.db";
    protected static final int DATABASE_VERSION = 6;

    private static CardsInfoDbHelper instance;

    @VisibleForTesting
    public CardsInfoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Deprecated
    public static synchronized CardsInfoDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CardsInfoDbHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CardDataSource.generateCreateTable());
        db.execSQL(DeckDataSource.generateCreateTable());
        db.execSQL(DeckDataSource.generateCreateTableJoin());
        db.execSQL(PlayerDataSource.generateCreateTable());
        db.execSQL(FavouritesDataSource.generateCreateTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3 && newVersion >= 3) {
            db.execSQL(PlayerDataSource.generateCreateTable());
            db.execSQL(FavouritesDataSource.generateCreateTable());
        }
        if (oldVersion < 4 && newVersion >= 4) {
            db.execSQL(DeckDataSource.generateCreateTable());
            db.execSQL(DeckDataSource.generateCreateTableJoin());
        }
        Set<String> columns = readColumnTable(db, CardDataSource.TABLE);
        if (!columns.contains(CardDataSource.COLUMNS.LAYOUT.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_LAYOUT);
        }
        if (!columns.contains(CardDataSource.COLUMNS.RULINGS.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_RULINGS);
        }
        if (!columns.contains(CardDataSource.COLUMNS.SET_CODE.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_SET_CODE);
        }
        if (!columns.contains(CardDataSource.COLUMNS.NUMBER.getName())) {
            db.execSQL(CardDataSource.SQL_ADD_COLUMN_NUMBER);
        }

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized void clear() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CardDataSource.TABLE, null, null);
        db.delete(DeckDataSource.TABLE, null, null);
        db.delete(DeckDataSource.TABLE_JOIN, null, null);
        db.delete(PlayerDataSource.TABLE, null, null);
        db.delete(FavouritesDataSource.TABLE, null, null);
    }

    public Set<String> readColumnTable(SQLiteDatabase db, String table) {
        Cursor dbCursor = db.rawQuery("PRAGMA table_info(MTGCard)", null);
        Set<String> columns = new HashSet<>(dbCursor.getCount());
        if (dbCursor.moveToFirst()) {
            do {
                columns.add(dbCursor.getString(1));
            } while (dbCursor.moveToNext());
        }
        dbCursor.close();
        return columns;
    }

    public List<MTGCard> loadDeck(Deck deck) {
        return DeckDataSource.getCards(getReadableDatabase(), deck);
    }

    public List<MTGCard> loadDeck(long deckId) {
        return DeckDataSource.getCards(getReadableDatabase(), deckId);
    }

    public List<Deck> getDecks() {
        return DeckDataSource.getDecks(getReadableDatabase());
    }

    public long addDeck(String name) {
        return DeckDataSource.addDeck(getWritableDatabase(), name);
    }

    public void deleteDeck(Deck deck) {
        DeckDataSource.deleteDeck(getWritableDatabase(), deck);
    }

    public void editDeck(Deck deck, String name) {
        DeckDataSource.renameDeck(getWritableDatabase(), deck.getId(), name);
    }

    public void addCard(Deck deck, MTGCard card, int quantity) {
        DeckDataSource.addCardToDeck(getWritableDatabase(), deck.getId(), card, quantity);
    }

    public void addCard(long deckId, MTGCard card, int quantity) {
        DeckDataSource.addCardToDeck(getWritableDatabase(), deckId, card, quantity);
    }

    public void removeAllCards(Deck deck, MTGCard card) {
        DeckDataSource.removeCardFromDeck(getWritableDatabase(), deck.getId(), card);
    }

    public void moveCardFromSideboard(Deck deck, MTGCard card, int quantity) {
        DeckDataSource.moveCardFromSideBoard(getWritableDatabase(), deck.getId(), card, quantity);
    }

    public void moveCardToSideboard(Deck deck, MTGCard card, int quantity) {
        DeckDataSource.moveCardToSideBoard(getWritableDatabase(), deck.getId(), card, quantity);
    }

    public List<Deck> addDeck(MTGCardDataSource cardDataSource, CardsBucket bucket) {
        DeckDataSource.addDeck(cardDataSource, getWritableDatabase(), bucket);
        return DeckDataSource.getDecks(getReadableDatabase());
    }
}
