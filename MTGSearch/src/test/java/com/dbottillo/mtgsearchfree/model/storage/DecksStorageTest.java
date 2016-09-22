package com.dbottillo.mtgsearchfree.model.storage;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.util.FileUtil;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class DecksStorageTest extends BaseTest {

    private static CardsInfoDbHelper cardsInfoDbHelper;
    private static MTGCardDataSource mtgCardDataSource;
    private static Deck deck;
    private static MTGCard card;
    private static FileUtil fileUtil;
    private static CardsBucket cardsBucket;
    private static List<MTGCard> deckCards = Arrays.asList(new MTGCard(18), new MTGCard(19));
    private static List<Deck> decks = Arrays.asList(new Deck(1), new Deck(2));

    private DecksStorage storage;

    @BeforeClass
    public static void staticSetup() {
        deck = mock(Deck.class);
        card = mock(MTGCard.class);
        fileUtil = mock(FileUtil.class);
        cardsBucket = mock(CardsBucket.class);
    }

    @Before
    public void setupStorage() {
        cardsInfoDbHelper = mock(CardsInfoDbHelper.class);
        mtgCardDataSource = mock(MTGCardDataSource.class);
        when(cardsInfoDbHelper.getDecks()).thenReturn(decks);
        when(cardsInfoDbHelper.loadDeck(deck)).thenReturn(deckCards);
        when(cardsInfoDbHelper.loadDeck(2L)).thenReturn(deckCards);
        when(cardsInfoDbHelper.addDeck("deck2")).thenReturn(2L);
        when(cardsInfoDbHelper.addDeck(mtgCardDataSource, cardsBucket)).thenReturn(decks);
        storage = new DecksStorage(fileUtil, cardsInfoDbHelper, mtgCardDataSource);
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
        verify(cardsInfoDbHelper).addDeck("deck");
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
        verify(cardsInfoDbHelper).addDeck("deck2");
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

    @Test
    public void DecksStorage_willImportDeck() throws Throwable {
        Uri uri = mock(Uri.class);
        when(fileUtil.readFileContent(uri)).thenReturn(cardsBucket);
        List<Deck> decksLoaded = storage.importDeck(uri);
        verify(cardsInfoDbHelper).addDeck(mtgCardDataSource, cardsBucket);
        assertNotNull(decksLoaded);
        assertThat(decksLoaded, is(decks));
    }

    @Test(expected=Throwable.class)
    public void DecksStorage_willNotImportNullDeck() throws Throwable {
        Uri uri = mock(Uri.class);
        Throwable throwable = new Throwable("error");
        when(fileUtil.readFileContent(uri)).thenThrow(throwable);
        storage.importDeck(uri);
    }
}