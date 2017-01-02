package com.dbottillo.mtgsearchfree.interactors;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode;
import com.dbottillo.mtgsearchfree.exceptions.MTGException;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.Logger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DecksInteractorImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Deck deck;
    @Mock
    MTGCard card;
    @Mock
    DecksStorage storage;
    @Mock
    FileUtil fileUtil;
    @Mock
    Uri uri;
    @Mock
    Logger logger;

    private List<Deck> decks = Arrays.asList(new Deck(2), new Deck(3));
    private List<MTGCard> deckCards = Arrays.asList(new MTGCard(7), new MTGCard(8));

    private DecksInteractor underTest;

    @Before
    public void setup() {
        when(storage.load()).thenReturn(decks);
        when(storage.loadDeck(deck)).thenReturn(deckCards);
        when(storage.addDeck("deck")).thenReturn(decks);
        when(storage.deleteDeck(deck)).thenReturn(decks);
        when(storage.editDeck(deck, "new name")).thenReturn(deckCards);
        underTest = new DecksInteractorImpl(storage, fileUtil, logger);
    }

    @Test
    public void testLoad() {
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        underTest.load().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(decks));
    }

    @Test
    public void testLoadDeck() {
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        underTest.loadDeck(deck).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).loadDeck(deck);
    }

    @Test
    public void testAddDeck() {
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        underTest.addDeck("deck").subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(decks));
        verify(storage).addDeck("deck");
    }

    @Test
    public void testDeleteDeck() {
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        underTest.deleteDeck(deck).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(decks));
        verify(storage).deleteDeck(deck);
    }

    @Test
    public void testEditDeck() {
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        underTest.editDeck(deck, "new name").subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).editDeck(deck, "new name");
    }

    @Test
    public void testAddCard() {
        when(storage.addCard(deck, card, 2)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        underTest.addCard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).addCard(deck, card, 2);
    }

    @Test
    public void testAddCardWithNewDeck() {
        when(storage.addCard("name", card, 2)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        underTest.addCard("name", card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).addCard("name", card, 2);
    }

    @Test
    public void testRemoveCard() {
        when(storage.removeCard(deck, card)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        underTest.removeCard(deck, card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).removeCard(deck, card);
    }

    @Test
    public void testRemoveAllCard() {
        when(storage.removeAllCard(deck, card)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        underTest.removeAllCard(deck, card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).removeAllCard(deck, card);
    }

    @Test
    public void movesCardFromSideboard() {
        when(storage.moveCardFromSideboard(deck, card, 2)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        underTest.moveCardFromSideboard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).moveCardFromSideboard(deck, card, 2);
    }

    @Test
    public void movesCardToSideboard() {
        when(storage.moveCardToSideboard(deck, card, 2)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        underTest.moveCardToSideboard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).moveCardToSideboard(deck, card, 2);
    }

    @Test
    public void testImportDeck() throws Throwable {
        when(storage.importDeck(uri)).thenReturn(decks);
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        underTest.importDeck(uri).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(decks));
        verify(storage).importDeck(uri);
    }

    @Test
    public void exportsDeck() {
        when(fileUtil.downloadDeckToSdCard(deck, deckCards)).thenReturn(true);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        underTest.exportDeck(deck, deckCards).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(true));
    }

    @Test
    public void throwErrorIfImportFails() throws MTGException {
        MTGException exception = new MTGException(ExceptionCode.DECK_NOT_IMPORTED, "error");
        when(storage.importDeck(uri)).thenThrow(exception);
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        underTest.importDeck(uri).subscribe(testSubscriber);
        testSubscriber.assertError(exception);
    }
}