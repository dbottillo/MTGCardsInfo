package com.dbottillo.mtgsearchfree.database;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.model.MTGCard;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class CardDataSourceTest extends BaseDatabaseTest {

    @Test
    public void test_generate_table_is_correct() {
        String query = CardDataSource.generateCreateTable();
        assertNotNull(query);
        assertThat(query, is("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT)"));
        assertThat(CardDataSource.generateCreateTable(1), is("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT)"));
        assertThat(CardDataSource.generateCreateTable(2), is("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT)"));
    }

    @Test
    public void test_card_can_be_saved_in_database() {
        MTGCard card = mtgDatabaseHelper.getRandomCard(1).get(0);
        long id = CardDataSource.saveCard(cardsInfoDbHelper.getWritableDatabase(), card);
        Cursor cursor = cardsInfoDbHelper.getReadableDatabase().rawQuery("select * from " + CardDataSource.TABLE + " where rowid =?", new String[]{id + ""});
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(1));
        cursor.moveToFirst();
        MTGCard cardFromDb = CardDataSource.fromCursor(cursor, true);
        assertNotNull(cardFromDb);
        assertThat(cardFromDb.getName(), is(card.getName()));
        assertThat(cardFromDb.getType(), is(card.getType()));
        assertThat(cardFromDb.getSubTypes().size(), is(card.getSubTypes().size()));
        for (int i = 0; i < cardFromDb.getSubTypes().size(); i++) {
            assertThat(cardFromDb.getSubTypes().get(i), is(card.getSubTypes().get(i)));
        }
        assertThat(cardFromDb.getColors().size(), is(card.getColors().size()));
        for (int i = 0; i < cardFromDb.getColors().size(); i++) {
            assertThat(cardFromDb.getColors().get(i), is(card.getColors().get(i)));
        }
        assertThat(cardFromDb.getCmc(), is(card.getCmc()));
        assertThat(cardFromDb.getRarity(), is(card.getRarity()));
        assertThat(cardFromDb.getPower(), is(card.getPower()));
        assertThat(cardFromDb.getToughness(), is(card.getToughness()));
        assertThat(cardFromDb.getManaCost(), is(card.getManaCost()));
        assertThat(cardFromDb.getText(), is(card.getText()));
        assertThat(cardFromDb.isMultiColor(), is(card.isMultiColor()));
        assertThat(cardFromDb.isLand(), is(card.isLand()));
        assertThat(cardFromDb.isArtifact(), is(card.isArtifact()));
        assertThat(cardFromDb.isEldrazi(), is(card.isEldrazi()));
        assertThat(cardFromDb.getIdSet(), is(card.getIdSet()));
        assertThat(cardFromDb.getSetName(), is(card.getSetName()));
        assertThat(cardFromDb.getSetCode(), is(card.getSetCode()));
        assertThat(cardFromDb.getLayout(), is(card.getLayout()));
        assertThat(cardFromDb.getNumber(), is(card.getNumber()));
        assertThat(cardFromDb.getRulings().size(), is(card.getRulings().size()));
        for (int i = 0; i < cardFromDb.getRulings().size(); i++) {
            assertThat(cardFromDb.getRulings().get(i), is(card.getRulings().get(i)));
        }
        cursor.close();
    }

    @Test
    public void test_cards_can_be_retrieved_from_database() {
        MTGCard card = new MTGCard();
        card.setMultiVerseId(101);
        CardDataSource.saveCard(cardsInfoDbHelper.getWritableDatabase(), card);
        MTGCard card2 = new MTGCard();
        card2.setMultiVerseId(102);
        CardDataSource.saveCard(cardsInfoDbHelper.getWritableDatabase(), card2);
        Cursor cursor = cardsInfoDbHelper.getReadableDatabase().rawQuery("select * from " + CardDataSource.TABLE, null);
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
        assertThat(cards.get(0).getMultiVerseId(), is(card.getMultiVerseId()));
        assertThat(cards.get(1).getMultiVerseId(), is(card2.getMultiVerseId()));
    }

}