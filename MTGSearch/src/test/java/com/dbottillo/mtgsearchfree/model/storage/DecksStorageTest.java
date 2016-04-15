package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DecksStorageTest extends BaseTest{

    static CardsInfoDbHelper cardsInfoDbHelper;
    static Deck deck;
    static MTGCard card;
    static List<MTGCard> deckCards = Arrays.asList(new MTGCard(18), new MTGCard(19));
    static List<Deck> decks = Arrays.asList(new Deck(1), new Deck(2));

    DecksStorage storage;

    @BeforeClass
    public static void staticSetup() {
        deck = mock(Deck.class);
        card = mock(MTGCard.class);
    }

    @Before
    public void setupStorage() {
        cardsInfoDbHelper = mock(CardsInfoDbHelper.class);
        when(cardsInfoDbHelper.getDecks()).thenReturn(decks);
        when(cardsInfoDbHelper.loadDeck(deck)).thenReturn(deckCards);
        when(cardsInfoDbHelper.loadDeck(2L)).thenReturn(deckCards);
        when(cardsInfoDbHelper.addDecK("deck2")).thenReturn(2L);
        storage = new DecksStorage(cardsInfoDbHelper);
    }

    @Test
    public void testLoad() {
        List<Deck> decksLoaded = storage.load();
        verify(cardsInfoDbHelper).getDecks();
        assertNotNull(decksLoaded);
        assertThat(decksLoaded, is(decks));
    }

    @Test
    public void testAddDeck() {
        storage.addDeck("deck");
        verify(cardsInfoDbHelper).addDecK("deck");
    }

    @Test
    public void testDeleteDeck() {
        storage.deleteDeck(deck);
        verify(cardsInfoDbHelper).deleteDeck(deck);
    }

    @Test
    public void testLoadDeck() {
        List<MTGCard> cards = storage.loadDeck(deck);
        verify(cardsInfoDbHelper).loadDeck(deck);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testEditDeck() {
        List<MTGCard> cards = storage.editDeck(deck, "new");
        verify(cardsInfoDbHelper).editDeck(deck, "new");
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testAddCard() {
        List<MTGCard> cards = storage.addCard(deck, card, 2);
        verify(cardsInfoDbHelper).addCard(deck, card, 2);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testAddCardNewDeck() {
        List<MTGCard> cards = storage.addCard("deck2", card, 2);
        verify(cardsInfoDbHelper).addDecK("deck2");
        verify(cardsInfoDbHelper).addCard(2L, card, 2);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testRemoveCard() {
        List<MTGCard> cards = storage.removeCard(deck, card);
        verify(cardsInfoDbHelper).addCard(deck, card, -1);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testRemoveAllCard() {
        List<MTGCard> cards = storage.removeAllCard(deck, card);
        verify(cardsInfoDbHelper).removeAllCards(deck, card);
        assertThat(cards, is(deckCards));
    }
}