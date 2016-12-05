package com.dbottillo.mtgsearchfree.model.storage;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode;
import com.dbottillo.mtgsearchfree.exceptions.MTGException;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.MTGExceptionMatcher;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DecksStorageTest extends BaseTest {

    private static final long DECK_ID = 200L;
    private static DeckDataSource deckDataSource;
    private static MTGCardDataSource mtgCardDataSource;
    private static Deck deck;
    private static MTGCard card;
    private static FileUtil fileUtil;
    private static CardsBucket cardsBucket;
    private static List<MTGCard> deckCards = Arrays.asList(new MTGCard(18), new MTGCard(19));
    private static List<Deck> decks = Arrays.asList(new Deck(1), new Deck(2));
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private DecksStorage underTest;

    @BeforeClass
    public static void staticSetup() {
        deck = mock(Deck.class);
        card = mock(MTGCard.class);
        fileUtil = mock(FileUtil.class);
        cardsBucket = mock(CardsBucket.class);
    }

    @Before
    public void setupStorage() {
        deckDataSource = mock(DeckDataSource.class);
        mtgCardDataSource = mock(MTGCardDataSource.class);
        when(deck.getId()).thenReturn(DECK_ID);
        when(deckDataSource.getDecks()).thenReturn(decks);
        when(deckDataSource.getDeck(DECK_ID)).thenReturn(deck);
        when(deckDataSource.addDeck("deck2")).thenReturn(2L);
        when(deckDataSource.addDeck(cardsBucket)).thenReturn(DECK_ID);
        when(deckDataSource.getCards(deck)).thenReturn(deckCards);
        when(deckDataSource.getCards(2L)).thenReturn(deckCards);
        underTest = new DecksStorage(fileUtil, deckDataSource);
    }

    @Test
    public void testLoad() {
        List<Deck> decksLoaded = underTest.load();
        verify(deckDataSource).getDecks();
        assertNotNull(decksLoaded);
        assertThat(decksLoaded, is(decks));
    }

    @Test
    public void testAddDeck() {
        underTest.addDeck("deck");
        verify(deckDataSource).addDeck("deck");
    }

    @Test
    public void testDeleteDeck() {
        underTest.deleteDeck(deck);
        verify(deckDataSource).deleteDeck(deck);
    }

    @Test
    public void testLoadDeck() {
        List<MTGCard> cards = underTest.loadDeck(deck);
        verify(deckDataSource).getCards(deck);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testEditDeck() {
        List<MTGCard> cards = underTest.editDeck(deck, "new");
        verify(deckDataSource).renameDeck(DECK_ID, "new");
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testAddCard() {
        List<MTGCard> cards = underTest.addCard(deck, card, 2);
        verify(deckDataSource).addCardToDeck(DECK_ID, card, 2);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testAddCardNewDeck() {
        List<MTGCard> cards = underTest.addCard("deck2", card, 2);
        verify(deckDataSource).addDeck("deck2");
        verify(deckDataSource).addCardToDeck(2L, card, 2);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testRemoveCard() {
        List<MTGCard> cards = underTest.removeCard(deck, card);
        verify(deckDataSource).addCardToDeck(DECK_ID, card, -1);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void movesCardFromSideboard() {
        List<MTGCard> cards = underTest.moveCardFromSideboard(deck, card, 2);
        verify(deckDataSource).moveCardFromSideBoard(DECK_ID, card, 2);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void movesCardToSideboard() {
        List<MTGCard> cards = underTest.moveCardToSideboard(deck, card, 2);
        verify(deckDataSource).moveCardToSideBoard(DECK_ID, card, 2);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testRemoveAllCard() {
        List<MTGCard> cards = underTest.removeAllCard(deck, card);
        verify(deckDataSource).removeCardFromDeck(DECK_ID, card);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void DecksStorage_willImportDeck() throws Throwable {
        Uri uri = mock(Uri.class);
        when(fileUtil.readFileContent(uri)).thenReturn(cardsBucket);
        List<Deck> decksLoaded = underTest.importDeck(uri);
        verify(deckDataSource).addDeck(cardsBucket);
        assertNotNull(decksLoaded);
        assertThat(decksLoaded, is(decks));
    }

    @Test
    public void DecksStorage_willNotImportNullDeck() throws Exception {
        exception.expect(MTGException.class);
        exception.expect(MTGExceptionMatcher.hasCode(ExceptionCode.DECK_NOT_IMPORTED));

        Uri uri = mock(Uri.class);
        Exception e = new Exception("error");
        when(fileUtil.readFileContent(uri)).thenThrow(e);
        underTest.importDeck(uri);
    }

}