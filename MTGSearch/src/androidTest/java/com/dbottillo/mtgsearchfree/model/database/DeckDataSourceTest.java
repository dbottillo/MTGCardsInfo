package com.dbottillo.mtgsearchfree.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DeckDataSourceTest extends BaseDatabaseTest {

    private static final int SMALL_NUMBER_OF_CARDS = 4;

    MTGCardDataSource cardDataSource;

    @Before
    public void setup(){
        cardDataSource = new MTGCardDataSource(mtgDatabaseHelper);
    }

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
        Deck deckFromDb = getDeck(id);
        assertNotNull(deckFromDb);
        assertThat(deckFromDb.getId(), is(id));
        assertThat(deckFromDb.getName(), is("deck"));
        assertThat(deckFromDb.isArchived(), is(false));
    }

    @Test
    public void test_deck_can_be_removed_from_database() {
        long id = DeckDataSource.addDeck(cardsInfoDbHelper.getWritableDatabase(), "deck");
        MTGCard card = cardDataSource.getRandomCard(1).get(0);
        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, 2);
        ArrayList<Deck> decks = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase());
        assertThat(decks.get(0).getNumberOfCards(), is(2));
        DeckDataSource.deleteDeck(cardsInfoDbHelper.getWritableDatabase(), decks.get(0));
        decks = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase());
        assertThat(decks.size(), is(0));
        // also need to test that the join table has been cleared
        Cursor cursor = cardsInfoDbHelper.getReadableDatabase().rawQuery("select * from " + DeckDataSource.TABLE_JOIN, null);
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
        MTGCard card = cardDataSource.getRandomCard(1).get(0);
        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, 2);
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
        MTGCard card = cardDataSource.getRandomCard(1).get(0);
        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, 2);
        ArrayList<Deck> decks = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase());
        assertThat(decks.get(0).getNumberOfCards(), is(2));
        DeckDataSource.removeCardFromDeck(cardsInfoDbHelper.getWritableDatabase(), id, card);
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
        MTGCard card = cardDataSource.getRandomCard(1).get(0);

        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, 4);
        Deck deck = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase()).get(0);
        assertThat(deck.getNumberOfCards(), is(4));

        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, -2);
        deck = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase()).get(0);
        assertThat(deck.getNumberOfCards(), is(2));

        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, -1);
        deck = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase()).get(0);
        assertThat(deck.getNumberOfCards(), is(1));

        DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, card, -4);
        deck = DeckDataSource.getDecks(cardsInfoDbHelper.getReadableDatabase()).get(0);
        assertThat(deck.getNumberOfCards(), is(0));
    }

    @Test
    public void test_add_sideboard_cards_are_independent() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        long id = DeckDataSource.addDeck(db, "new deck");
        MTGCard card = cardDataSource.getRandomCard(1).get(0);

        DeckDataSource.addCardToDeck(db, id, card, 2);
        card.setSideboard(true);
        DeckDataSource.addCardToDeck(db, id, card, 2);
        Deck deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(2));
        assertThat(deck.getSizeOfSideboard(), is(2));

        card.setSideboard(false);
        DeckDataSource.addCardToDeck(db, id, card, 2);
        card.setSideboard(true);
        DeckDataSource.addCardToDeck(db, id, card, -4);
        deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(4));
        assertThat(deck.getSizeOfSideboard(), is(0));

        card.setSideboard(false);
        DeckDataSource.addCardToDeck(db, id, card, -1);
        card.setSideboard(true);
        DeckDataSource.addCardToDeck(db, id, card, 6);
        deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(3));
        assertThat(deck.getSizeOfSideboard(), is(6));
    }

    @Test
    public void test_remove_sideboard_cards_are_independent() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        long id = DeckDataSource.addDeck(db, "new deck");
        MTGCard card = cardDataSource.getRandomCard(1).get(0);

        card.setSideboard(false);
        DeckDataSource.addCardToDeck(db, id, card, 2);
        card.setSideboard(true);
        DeckDataSource.addCardToDeck(db, id, card, 2);
        Deck deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(2));
        assertThat(deck.getSizeOfSideboard(), is(2));

        card.setSideboard(false);
        DeckDataSource.removeCardFromDeck(db, id, card);
        deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(0));
        assertThat(deck.getSizeOfSideboard(), is(2));

        card.setSideboard(true);
        DeckDataSource.removeCardFromDeck(db, id, card);
        deck = DeckDataSource.getDecks(db).get(0);
        assertThat(deck.getNumberOfCards(), is(0));
        assertThat(deck.getSizeOfSideboard(), is(0));
    }

    @Test
    public void test_add_deck_with_empty_bucket() {
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        SQLiteDatabase mtgdb = mtgDatabaseHelper.getWritableDatabase();
        CardsBucket bucket = new CardsBucket();
        bucket.setKey("deck");
        long deckId = DeckDataSource.addDeck(cardDataSource, db, bucket);
        assertTrue(deckId > 0);
        Deck deckFromDb = getDeck(deckId);
        assertThat(deckFromDb.getName(), is("deck"));
        assertThat(deckFromDb.getNumberOfCards(), is(0));
    }

    @Test
    public void test_add_deck_with_non_empty_bucket() {
        String[] cardNames = new String[]{"Counterspell", "Jace", "Liliana", "Fireball", "Zombie"};
        int[] quantities = new int[]{2, 3, 4, 1, 3};
        boolean[] side = new boolean[]{true, true, false, false, true};
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        SQLiteDatabase mtgdb = mtgDatabaseHelper.getWritableDatabase();
        CardsBucket bucket = new CardsBucket();
        bucket.setKey("deck");
        List<MTGCard> namedCards = new ArrayList<>(cardNames.length);
        for (int i = 0; i < cardNames.length; i++) {
            MTGCard card = new MTGCard();
            card.setCardName(cardNames[i]);
            card.setQuantity(quantities[i]);
            card.setSideboard(side[i]);
            namedCards.add(card);
        }
        bucket.setCards(namedCards);
        long deckId = DeckDataSource.addDeck(cardDataSource, db, bucket);
        assertTrue(deckId > 0);
        Deck deckFromDb = getDeck(deckId);
        assertThat(deckFromDb.getName(), is("deck"));
        List<MTGCard> deckCards = DeckDataSource.getCards(db, deckId);
        assertThat(deckCards.size(), is(cardNames.length));
        for (MTGCard card : deckCards) {
            boolean found = false;
            int index = 0;
            for (int i = 0; i < cardNames.length; i++) {
                if (card.getName().toLowerCase().contains(cardNames[i].toLowerCase())) {
                    found = true;
                    index = i;
                    break;
                }
            }
            assertTrue(found);
            assertThat(card.getQuantity(), is(quantities[index]));
            assertThat(card.isSideboard(), is(side[index]));
        }
    }

    @Test
    public void test_add_deck_with_ignore_non_card_name() {
        String[] cardNames = new String[]{"Counterspell", "Eistein"};
        int[] quantities = new int[]{2, 3};
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        SQLiteDatabase mtgdb = mtgDatabaseHelper.getWritableDatabase();
        CardsBucket bucket = new CardsBucket();
        bucket.setKey("deck");
        List<MTGCard> namedCards = new ArrayList<>(cardNames.length);
        for (int i = 0; i < cardNames.length; i++) {
            MTGCard card = new MTGCard();
            card.setCardName(cardNames[i]);
            card.setQuantity(quantities[i]);
            namedCards.add(card);
        }
        bucket.setCards(namedCards);
        long deckId = DeckDataSource.addDeck(cardDataSource, db, bucket);
        List<MTGCard> deckCards = DeckDataSource.getCards(db, deckId);
        assertThat(deckCards.size(), is(cardNames.length-1));
    }

    @Test
    public void DeckDataSource_willImportDeck(){

    }

    private long generateDeckWithSmallAmountOfCards() {
        long id = DeckDataSource.addDeck(cardsInfoDbHelper.getWritableDatabase(), "new deck");
        List<MTGCard> cards = cardDataSource.getRandomCard(SMALL_NUMBER_OF_CARDS);
        for (int i = 0; i < SMALL_NUMBER_OF_CARDS; i++) {
            DeckDataSource.addCardToDeck(cardsInfoDbHelper.getWritableDatabase(), id, cards.get(i), i + 1);
        }
        return id;
    }

    private Deck getDeck(long id) {
        Cursor cursor = cardsInfoDbHelper.getReadableDatabase().rawQuery("select * from " + DeckDataSource.TABLE + " where rowid =?", new String[]{id + ""});
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(1));
        cursor.moveToFirst();
        Deck deck = DeckDataSource.fromCursor(cursor);
        cursor.close();
        return deck;
    }

}