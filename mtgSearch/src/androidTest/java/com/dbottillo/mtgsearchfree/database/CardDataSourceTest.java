package com.dbottillo.mtgsearchfree.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.resources.MTGCard;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class CardDataSourceTest extends BaseDatabaseTest {

    @Test
    public void test_generate_table_is_correct() {
        String query = CardDataSource.generateCreateTable();
        assertNotNull(query);
        assertThat(query, is("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,setCode TEXT,rulings TEXT,layout TEXT,number TEXT)"));
    }

    @Test
    public void test_card_can_be_saved_in_database() {
        MTGCard card = new MTGCard();
        card.setId(1);
        card.setCardName("card");
        card.setType("Dragon");
        card.addType("Creature");
        card.addSubType("Goblin");
        card.addSubType("Artifact");
        card.addSubType("Wizard");
        card.addColor(MTGCard.BLACK);
        card.addColor(MTGCard.RED);
        card.setCmc(12);
        card.setRarity("Rare");
        card.setPower("4");
        card.setToughness("12");
        card.setManaCost("3UU");
        card.setText("card text");
        card.setMultiColor(false);
        card.setAsALand(true);
        card.setAsArtifact(true);
        card.setAsEldrazi(false);
        card.setMultiVerseId(8743);
        card.setIdSet(3);
        card.setSetName("COmmander");
        card.setSetCode("CMX");
        card.setLayout("split");
        card.setNumber("30a");
        card.addRuling("rule");
        card.addRuling("rule2");
        long id = CardDataSource.saveCard(dataHelper.getWritableDatabase(), card);
        Cursor cursor = dataHelper.getReadableDatabase().rawQuery("select * from " + CardDataSource.TABLE + " where rowid =?", new String[]{id + ""});
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(1));
        cursor.moveToFirst();
        MTGCard cardFromDb = CardDataSource.fromCursor(cursor, true);
        assertNotNull(cardFromDb);
        assertThat(cardFromDb.getId(), is(card.getId()));
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
    }
}