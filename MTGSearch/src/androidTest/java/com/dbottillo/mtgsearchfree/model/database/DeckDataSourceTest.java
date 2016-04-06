package com.dbottillo.mtgsearchfree.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DeckDataSourceTest extends BaseDatabaseTest {

    private static final int SMALL_NUMBER_OF_CARDS = 4;

    @Test
    public void generate_table_is_correct() {
        String query = DeckDataSource.generateCreateTable();
        String queryJoin = DeckDataSource.generateCreateTableJoin();
        assertNotNull(query);
        assertThat(query, is("CREATE TABLE IF NOT EXISTS decks (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT not null,color TEXT,archived INT)"));
        assertThat(queryJoin, is("CREATE TABLE IF NOT EXISTS deck_card (deck_id INT not null,card_id INT not null,quantity INT not null,side INT)"));
    }

    @Test
    public void test_deck_can_be_saved_in_database() {
        long id = DeckDataSource.addDeck(cardsInfoDbHelper.getWritableDatabase(), "deck");
        Cursor cursor = cardsInfoDbHelper.getReadableDatabase().rawQuery("select * from " + DeckDataSource.TABLE + " where rowid =?", new String[]{id + ""});
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(1));
        cursor.moveToFirst();
        Deck deckFromDb = DeckDataSource.fromCursor(cursor);
        assertNotNull(deckFromDb);
        assertThat(deckFromDb.getId(), is(id));
        assertThat(deckFromDb.getName(), is("deck"));
        assertThat(deckFromDb.isArchived(), is(false));
        cursor.close();
    }

    @Test
    public void test_deck_can_be_removed_from_database() {
        long id = DeckDataSource.addDeck(cardsInfoDbHelper.getWritableDatabase(), "deck");
        MTGCard card = mtgDatabaseHelper.getRandomCard(1).get(0);
        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, 2, false);
        ArrayList<Deck> decks = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase());
        assertThat(decks.get(0).getNumberOfCards(), is(2));
        DeckDataSource.deleteDeck(cardsInfoDbHelper.getWritableDatabase(), decks.get(0));
        decks = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase());
        assertThat(decks.size(), is(0));
        // also need to test that the join table has been cleared
        Cursor cursor = cardsInfoDbHelper.getReadableDatabase().rawQuery("select * from " + DeckDataSource.TABLE_JOIN , null);
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(0));
        cursor.close();
    }

    @Test
    public void DeckDataSource_nameDeckCanBeEdited() {
        long id = DeckDataSource.addDeck(cardsInfoDbHelper.getWritableDatabase(), "deck");
        int updatedId = DeckDataSource.renameDeck(cardsInfoDbHelper.getWritableDatabase(), id, "New name");
        assertThat((updatedId > -1), is(true));
        ArrayList<Deck> decks = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase());
        assertThat(decks.get(0).getName(), is("New name"));
    }

    @Test
    public void test_deck_cards_can_be_retrieved_from_database() {
        generateDeckWithSmallAmountOfCards();
        Deck deck = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase()).get(0);
        List<MTGCard> cards = DeckDataSource.getCards(cardsInfoDbHelper.getReadableDatabase(), deck);
        assertNotNull(cards);
        assertThat(cards.size(), is(SMALL_NUMBER_OF_CARDS));
    }

    @Test
    public void test_cards_can_be_added_to_deck() {
        long id = DeckDataSource.addDeck(cardsInfoDbHelper.getWritableDatabase(), "new deck");
        MTGCard card = mtgDatabaseHelper.getRandomCard(1).get(0);
        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, 2, false);
        // first check that the card has been saved in the db
        Cursor cursor = cardsInfoDbHelper.getReadableDatabase().rawQuery("select * from " + CardDataSource.TABLE + " where multiVerseId =?", new String[]{card.getMultiVerseId() + ""});
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(1));
        cursor.moveToFirst();
        MTGCard cardFromDb = CardDataSource.fromCursor(cursor, true);
        assertNotNull(cardFromDb);
        assertThat(cardFromDb.getMultiVerseId(), is(card.getMultiVerseId()));
        // then check that the decks contain at least one card
        ArrayList<Deck> decks = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase());
        assertThat(decks.size(), is(1));
        Deck deck = decks.get(0);
        assertThat(deck.getId(), is(id));
        assertThat(deck.getNumberOfCards(), is(2));
    }

    @Test
    public void test_cards_can_be_removed_from_deck() {
        long id = DeckDataSource.addDeck(cardsInfoDbHelper.getWritableDatabase(), "new deck");
        MTGCard card = mtgDatabaseHelper.getRandomCard(1).get(0);
        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, 2, false);
        ArrayList<Deck> decks = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase());
        assertThat(decks.get(0).getNumberOfCards(), is(2));
        DeckDataSource.removeCardFromDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, false);
        decks = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase());
        assertThat(decks.get(0).getNumberOfCards(), is(0));
    }

    @Test
    public void test_multiple_cards_can_be_added_to_deck() {
        long id = generateDeckWithSmallAmountOfCards();
        ArrayList<Deck> decks = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase());
        assertThat(decks.size(), is(1));
        Deck deck = decks.get(0);
        assertThat(deck.getId(), is(id));
        assertThat(deck.getNumberOfCards(), is(10));
    }

    @Test
    public void test_negative_quantity_will_decrease_cards() {
        long id = DeckDataSource.addDeck(cardsInfoDbHelper.getWritableDatabase(), "new deck");
        MTGCard card = mtgDatabaseHelper.getRandomCard(1).get(0);

        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, 4, false);
        Deck deck = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase()).get(0);
        assertThat(deck.getNumberOfCards(), is(4));

        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, -2, false);
        deck = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase()).get(0);
        assertThat(deck.getNumberOfCards(), is(2));

        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, -1, false);
        deck = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase()).get(0);
        assertThat(deck.getNumberOfCards(), is(1));

        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, -4, false);
        deck = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase()).get(0);
        assertThat(deck.getNumberOfCards(), is(0));
    }

    @Test
    public void test_add_sideboard_cards_are_independent() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        long id = DeckDataSource.addDeck(db, "new deck");
        MTGCard card = mtgDatabaseHelper.getRandomCard(1).get(0);

        DeckDataSource.addCardToDeck(db, id, card, 2, false);
        DeckDataSource.addCardToDeck(db, id, card, 2, true);
        Deck deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(2));
        assertThat(deck.getSizeOfSideboard(), is(2));

        DeckDataSource.addCardToDeck(db, id, card, 2, false);
        DeckDataSource.addCardToDeck(db, id, card, -4, true);
        deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(4));
        assertThat(deck.getSizeOfSideboard(), is(0));

        DeckDataSource.addCardToDeck(db, id, card, -1, false);
        DeckDataSource.addCardToDeck(db, id, card, 6, true);
        deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(3));
        assertThat(deck.getSizeOfSideboard(), is(6));
    }

    @Test
    public void test_remove_sideboard_cards_are_independent() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        long id = DeckDataSource.addDeck(db, "new deck");
        MTGCard card = mtgDatabaseHelper.getRandomCard(1).get(0);

        DeckDataSource.addCardToDeck(db, id, card, 2, false);
        DeckDataSource.addCardToDeck(db, id, card, 2, true);
        Deck deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(2));
        assertThat(deck.getSizeOfSideboard(), is(2));

        DeckDataSource.removeCardFromDeck(db, id, card, false);
        deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(0));
        assertThat(deck.getSizeOfSideboard(), is(2));

        DeckDataSource.removeCardFromDeck(db, id, card, true);
        deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(0));
        assertThat(deck.getSizeOfSideboard(), is(0));
    }

    private long generateDeckWithSmallAmountOfCards() {
        long id = DeckDataSource.addDeck(cardsInfoDbHelper.getWritableDatabase(), "new deck");
        List<MTGCard> cards = mtgDatabaseHelper.getRandomCard(SMALL_NUMBER_OF_CARDS);
        for (int i = 0; i < SMALL_NUMBER_OF_CARDS; i++) {
            DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, cards.get(i), i + 1, false);
        }
        return id;
    }

}