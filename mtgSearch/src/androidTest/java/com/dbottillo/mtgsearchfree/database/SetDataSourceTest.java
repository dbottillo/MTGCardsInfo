package com.dbottillo.mtgsearchfree.database;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.resources.MTGSet;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SetDataSourceTest extends BaseDatabaseTest {

    @Test
    public void test_generate_table_is_correct() {
        String query = SetDataSource.generateCreateTable();
        assertNotNull(query);
        assertThat(query, is("CREATE TABLE IF NOT EXISTS MTGSet (_id INTEGER PRIMARY KEY, name TEXT,code TEXT)"));
    }

    @Test
    public void test_set_can_be_saved_in_database() {
        MTGSet set = new MTGSet(50);
        set.setName("Commander");
        set.setCode("CMX");
        long id = SetDataSource.saveSet(dataHelper.getWritableDatabase(), set);
        Cursor cursor = dataHelper.getReadableDatabase().rawQuery("select * from " + SetDataSource.TABLE + " where rowid =?", new String[]{id + ""});
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(1));
        cursor.moveToFirst();
        MTGSet setFromDb = SetDataSource.fromCursor(cursor);
        assertNotNull(setFromDb);
        assertThat(setFromDb.getId(), is(set.getId()));
        assertThat(setFromDb.getName(), is(set.getName()));
        assertThat(setFromDb.getCode(), is(set.getCode()));
        cursor.close();
    }
/*
    @Test
    public void test_card_are_unique_in_database() {
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        int uniqueId = 444;
        MTGCard card = new MTGCard();
        card.setId(uniqueId);
        card.setCardName("one");
        long id = CardDataSource.saveCard(db, card);
        MTGCard card2 = new MTGCard();
        card2.setId(uniqueId);
        card2.setCardName("two");
        long id2 = CardDataSource.saveCard(db, card2);
        assertNotSame(id, id2);
        Cursor cursor = db.rawQuery("select * from " + CardDataSource.TABLE + " where _id =?", new String[]{uniqueId + ""});
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(1));
        cursor.moveToFirst();
        MTGCard cardFromDb = CardDataSource.fromCursor(cursor);
        assertThat(cardFromDb.getName(), is(card.getName())); // same id are ignored
        cursor.close();
    }

    @Test
    public void test_cards_can_be_retrieved_from_database() {
        MTGCard card = new MTGCard();
        card.setId(101);
        CardDataSource.saveCard(dataHelper.getWritableDatabase(), card);
        MTGCard card2 = new MTGCard();
        card2.setId(102);
        CardDataSource.saveCard(dataHelper.getWritableDatabase(), card2);
        Cursor cursor = dataHelper.getReadableDatabase().rawQuery("select * from " + CardDataSource.TABLE, null);
        ArrayList<MTGCard> cards = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                cards.add(CardDataSource.fromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        assertNotNull(cards);
        assertThat(cards.size(), is(2));
        assertThat(cards.get(0).getId(), is(card.getId()));
        assertThat(cards.get(1).getId(), is(card2.getId()));
    }*/
}