package com.dbottillo.mtgsearchfree.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.resources.Player;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class CardsInfoDbHelperTest extends BaseDatabaseTest {

    @Test
    public void test_tables_are_created() {
        Set<String> tables = readTables(dataHelper);
        assertThat(tables.size(), is(7)); // android_metadata + sqlite_sequence + number of tables required
        assertThat(tables.contains(CardContract.CardEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(true));
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(true));
    }

    @Test
    public void test_tables_are_cleared() {
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        addDummyData(db);
        assertRowDatabaseNumber(db, CardContract.CardEntry.TABLE_NAME, 1L);
        assertRowDatabaseNumber(db, DeckDataSource.TABLE_DECKS, 1L);
        assertRowDatabaseNumber(db, DeckDataSource.TABLE_DECK_CARD, 1L);
        assertRowDatabaseNumber(db, PlayerDataSource.PlayerEntry.TABLE_NAME, 1L);
        assertRowDatabaseNumber(db, FavouritesDataSource.FavouritesEntry.TABLE_NAME, 1L);
        dataHelper.clear();
        assertRowDatabaseNumber(db, CardContract.CardEntry.TABLE_NAME, 0L);
        assertRowDatabaseNumber(db, DeckDataSource.TABLE_DECKS, 0L);
        assertRowDatabaseNumber(db, DeckDataSource.TABLE_DECK_CARD, 0L);
        assertRowDatabaseNumber(db, PlayerDataSource.PlayerEntry.TABLE_NAME, 0L);
        assertRowDatabaseNumber(db, FavouritesDataSource.FavouritesEntry.TABLE_NAME, 0L);
    }

    @Test
    public void test_db_upgrade_from_version1_to_version2() {
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        downgradeDb(db, 1);
        Set<String> tables = readTables(dataHelper);
        assertThat(tables.contains(CardContract.CardEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(false));
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(false));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(false));
        dataHelper.onUpgrade(db, 1, 2);
        // TODO: check that rulings column has been added
    }

    @Test
    public void test_db_upgrade_from_version1_to_version3() {
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        downgradeDb(db, 1);
        Set<String> tables = readTables(dataHelper);
        assertThat(tables.contains(CardContract.CardEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(false));
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(false));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(false));
        dataHelper.onUpgrade(db, 1, 3);
        // TODO: check that rulings column has been added
        tables = readTables(dataHelper);
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(true));
    }

    @Test
    public void test_db_upgrade_from_version1_to_version4() {
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        downgradeDb(db, 1);
        Set<String> tables = readTables(dataHelper);
        assertThat(tables.contains(CardContract.CardEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(false));
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(false));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(false));
        dataHelper.onUpgrade(db, 1, 4);
        // TODO: check that rulings column has been added
        tables = readTables(dataHelper);
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(true));
        // TODO: check that number and setCode column has been added
    }

    @Test
    public void test_db_upgrade_from_version2_to_version3() {
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        downgradeDb(db, 2);
        Set<String> tables = readTables(dataHelper);
        assertThat(tables.contains(CardContract.CardEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(false));
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(false));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(false));
        dataHelper.onUpgrade(db, 2, 3);
        tables = readTables(dataHelper);
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(false));
    }

    @Test
    public void test_db_upgrade_from_version2_to_version4() {
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        downgradeDb(db, 2);
        Set<String> tables = readTables(dataHelper);
        assertThat(tables.contains(CardContract.CardEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(false));
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(false));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(false));
        dataHelper.onUpgrade(db, 2, 4);
        tables = readTables(dataHelper);
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(true));
        // TODO: check that number and setCode column has been added
    }

    @Test
    public void test_db_upgrade_from_version3_to_version4() {
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        downgradeDb(db, 3);
        Set<String> tables = readTables(dataHelper);
        assertThat(tables.contains(CardContract.CardEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(false));
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(true));
        dataHelper.onUpgrade(db, 3, 4);
        tables = readTables(dataHelper);
        // TODO: check that number and setCode column has been added
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(true));
    }

    private void assertRowDatabaseNumber(SQLiteDatabase db, String table, long howMany) {
        assertThat(db.compileStatement("select count(*) from " + table + ";").simpleQueryForLong(), is(howMany));
    }

    private Set<String> readColumnTable(SQLiteDatabase db, String table) {
        // TODO
        return null;
    }

    private Set<String> readTables(CardsInfoDbHelper dataHelper) {
        return readTables(dataHelper.getReadableDatabase());
    }

    private Set<String> readTables(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        Set<String> tables = new HashSet<>(cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tables.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return tables;
    }

    private void addDummyData(SQLiteDatabase db) {
        MTGCard card = new MTGCard();
        long deck = DeckDataSource.addDeck(db, "deck");
        DeckDataSource.addCardToDeck(db, deck, card, 2, false);
        PlayerDataSource.savePlayer(db, new Player(20, "liliana"));
        FavouritesDataSource.saveFavourites(db, card);
    }

    private void downgradeDb(SQLiteDatabase db, int version) {
        db.execSQL("DROP TABLE " + CardContract.CardEntry.TABLE_NAME);
        db.execSQL("DROP TABLE " + DeckDataSource.TABLE_DECKS);
        db.execSQL("DROP TABLE " + DeckDataSource.TABLE_DECK_CARD);
        db.execSQL("DROP TABLE " + FavouritesDataSource.FavouritesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE " + PlayerDataSource.PlayerEntry.TABLE_NAME);
        if (version == 1) {
            db.execSQL(CardContract.SQL_CREATE_CARDS_TABLE_VERSION1);
        }
        if (version == 2) {
            db.execSQL(CardContract.SQL_CREATE_CARDS_TABLE_VERSION2);
        }
        if (version == 3) {
            db.execSQL(CardContract.SQL_CREATE_CARDS_TABLE_VERSION2);
            db.execSQL(PlayerDataSource.CREATE_PLAYERS_TABLE);
            db.execSQL(FavouritesDataSource.CREATE_FAVOURITES_TABLE);
        }
    }

}