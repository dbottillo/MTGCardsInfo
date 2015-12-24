package com.dbottillo.mtgsearchfree.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class CardsInfoDbHelperTest extends BaseDatabaseTest {

    @Test
    public void test_tables_are_created() {
        Set<String> tables = readTables(dataHelper);
        assertThat(tables.size(), is(6)); // android_metadata + number of tables required
        assertThat(tables.contains(CardContract.CardEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECKS), is(true));
        assertThat(tables.contains(DeckDataSource.TABLE_DECK_CARD), is(true));
        assertThat(tables.contains(PlayerDataSource.PlayerEntry.TABLE_NAME), is(true));
        assertThat(tables.contains(FavouritesDataSource.FavouritesEntry.TABLE_NAME), is(true));
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

}