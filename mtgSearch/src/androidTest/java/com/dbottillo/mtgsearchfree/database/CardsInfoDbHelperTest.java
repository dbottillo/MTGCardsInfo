package com.dbottillo.mtgsearchfree.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.Player;

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
        Set<String> tables = readTables(cardsInfoDbHelper);
        assertThat(tables.size(), is(7)); // android_metadata + sqlite_sequence + number of tables required
        assertThat(tables.contains(CardDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), is(true));
        assertThat(tables.contains(PlayerDataSource.TABLE), is(true));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(true));
    }

    @Test
    public void test_tables_are_cleared() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        addDummyData(db);
        assertRowDatabaseNumber(db, CardDataSource.TABLE, 1L);
        assertRowDatabaseNumber(db, DeckDataSource.TABLE, 1L);
        assertRowDatabaseNumber(db, DeckDataSource.TABLE_JOIN, 1L);
        assertRowDatabaseNumber(db, PlayerDataSource.TABLE, 1L);
        assertRowDatabaseNumber(db, FavouritesDataSource.TABLE, 1L);
        cardsInfoDbHelper.clear();
        assertRowDatabaseNumber(db, CardDataSource.TABLE, 0L);
        assertRowDatabaseNumber(db, DeckDataSource.TABLE, 0L);
        assertRowDatabaseNumber(db, DeckDataSource.TABLE_JOIN, 0L);
        assertRowDatabaseNumber(db, PlayerDataSource.TABLE, 0L);
        assertRowDatabaseNumber(db, FavouritesDataSource.TABLE, 0L);
    }

    @Test
    public void test_db_upgrade_from_version1_to_version2() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        downgradeDb(db, 1);
        Set<String> tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(CardDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), is(false));
        assertThat(tables.contains(PlayerDataSource.TABLE), is(false));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(false));
        Set<String> columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(false));
        cardsInfoDbHelper.onUpgrade(db, 1, 2);
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(true));
    }

    @Test
    public void test_db_upgrade_from_version1_to_version3() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        downgradeDb(db, 1);
        Set<String> tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(CardDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), is(false));
        assertThat(tables.contains(PlayerDataSource.TABLE), is(false));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(false));
        Set<String> columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(false));
        cardsInfoDbHelper.onUpgrade(db, 1, 3);
        tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(PlayerDataSource.TABLE), is(true));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(true));
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(true));
    }

    @Test
    public void test_db_upgrade_from_version1_to_version4() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        downgradeDb(db, 1);
        Set<String> tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(CardDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), is(false));
        assertThat(tables.contains(PlayerDataSource.TABLE), is(false));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(false));
        Set<String> columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(false));
        cardsInfoDbHelper.onUpgrade(db, 1, 4);
        tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(PlayerDataSource.TABLE), is(true));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(true));
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(true));
    }

    @Test
    public void test_db_upgrade_from_version2_to_version3() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        downgradeDb(db, 2);
        Set<String> tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(CardDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), is(false));
        assertThat(tables.contains(PlayerDataSource.TABLE), is(false));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(false));
        Set<String> columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(false));
        cardsInfoDbHelper.onUpgrade(db, 2, 3);
        tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(PlayerDataSource.TABLE), is(true));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), is(false));
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(true));
    }

    @Test
    public void test_db_upgrade_from_version2_to_version4() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        downgradeDb(db, 2);
        Set<String> tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(CardDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), is(false));
        assertThat(tables.contains(PlayerDataSource.TABLE), is(false));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(false));
        Set<String> columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(false));
        cardsInfoDbHelper.onUpgrade(db, 2, 4);
        tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(PlayerDataSource.TABLE), is(true));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), is(true));
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.RULINGS.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(true));
    }

    @Test
    public void test_db_upgrade_from_version3_to_version4() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        downgradeDb(db, 3);
        Set<String> tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(CardDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE), is(false));
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), is(false));
        assertThat(tables.contains(PlayerDataSource.TABLE), is(true));
        assertThat(tables.contains(FavouritesDataSource.TABLE), is(true));
        Set<String> columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(false));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(false));
        cardsInfoDbHelper.onUpgrade(db, 3, 4);
        tables = readTables(cardsInfoDbHelper);
        assertThat(tables.contains(DeckDataSource.TABLE), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_JOIN), is(true));
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.NUMBER.getName()), is(true));
        assertThat(columns.contains(CardDataSource.COLUMNS.SET_CODE.getName()), is(true));
    }

    @Test
    public void test_db_upgrade_from_version4_to_version5_without_layout() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        downgradeDb(db, 4);
        removeLayoutColumn(db);
        Set<String> columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(false));
        cardsInfoDbHelper.onUpgrade(db, 4, 5);
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(true));
    }

    @Test
    public void test_db_upgrade_from_version4_to_version5_does_not_generate_error() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        downgradeDb(db, 4);
        Set<String> columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(true));
        cardsInfoDbHelper.onUpgrade(db, 4, 5);
        columns = cardsInfoDbHelper.readColumnTable(db, CardDataSource.TABLE);
        assertThat(columns.contains(CardDataSource.COLUMNS.LAYOUT.getName()), is(true));
    }

    private void assertRowDatabaseNumber(SQLiteDatabase db, String table, long howMany) {
        assertThat(db.compileStatement("select count(*) from " + table + ";").simpleQueryForLong(), is(howMany));
    }

    private Set<String> readTables(CardsInfoDbHelper cardsInfoDbHelper) {
        return readTables(cardsInfoDbHelper.getReadableDatabase());
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
        db.execSQL("DROP TABLE " + CardDataSource.TABLE);
        db.execSQL("DROP TABLE " + DeckDataSource.TABLE);
        db.execSQL("DROP TABLE " + DeckDataSource.TABLE_JOIN);
        db.execSQL("DROP TABLE " + FavouritesDataSource.TABLE);
        db.execSQL("DROP TABLE " + PlayerDataSource.TABLE);
        if (version == 1) {
            db.execSQL(CardDataSource.generateCreateTable(1));
        }
        if (version == 2) {
            db.execSQL(CardDataSource.generateCreateTable(2));
        }
        if (version == 3) {
            db.execSQL(CardDataSource.generateCreateTable(2));
            db.execSQL(PlayerDataSource.generateCreateTable());
            db.execSQL(FavouritesDataSource.generateCreateTable());
        }
        if (version == 4) {
            db.execSQL(CardDataSource.generateCreateTable());
            db.execSQL(PlayerDataSource.generateCreateTable());
            db.execSQL(FavouritesDataSource.generateCreateTable());
        }
    }

    private void removeLayoutColumn(SQLiteDatabase db) {
        db.execSQL("DROP TABLE " + CardDataSource.TABLE);
        db.execSQL(CardDataSource.generateCreateTableWithoutLayout());
    }

}