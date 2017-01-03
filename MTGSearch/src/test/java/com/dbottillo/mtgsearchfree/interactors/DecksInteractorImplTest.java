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
import java.util.List;

import io.reactivex.observers.TestObserver;

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
        TestObserver<List<Deck>> testSubscriber = new TestObserver<>();
        underTest.load().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(decks);
    }

    @Test
    public void testLoadDeck() {
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.loadDeck(deck).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(storage).loadDeck(deck);
    }

    @Test
    public void testAddDeck() {
        TestObserver<List<Deck>> testSubscriber = new TestObserver<>();
        underTest.addDeck("deck").subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(decks);
        verify(storage).addDeck("deck");
    }

    @Test
    public void testDeleteDeck() {
        TestObserver<List<Deck>> testSubscriber = new TestObserver<>();
        underTest.deleteDeck(deck).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(decks);
        verify(storage).deleteDeck(deck);
    }

    @Test
    public void testEditDeck() {
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.editDeck(deck, "new name").subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(storage).editDeck(deck, "new name");
    }

    @Test
    public void testAddCard() {
        when(storage.addCard(deck, card, 2)).thenReturn(deckCards);
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.addCard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(storage).addCard(deck, card, 2);
    }

    @Test
    public void testAddCardWithNewDeck() {
        when(storage.addCard("name", card, 2)).thenReturn(deckCards);
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.addCard("name", card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(storage).addCard("name", card, 2);
    }

    @Test
    public void testRemoveCard() {
        when(storage.removeCard(deck, card)).thenReturn(deckCards);
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.removeCard(deck, card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(storage).removeCard(deck, card);
    }

    @Test
    public void testRemoveAllCard() {
        when(storage.removeAllCard(deck, card)).thenReturn(deckCards);
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.removeAllCard(deck, card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(storage).removeAllCard(deck, card);
    }

    @Test
    public void movesCardFromSideboard() {
        when(storage.moveCardFromSideboard(deck, card, 2)).thenReturn(deckCards);
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.moveCardFromSideboard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(storage).moveCardFromSideboard(deck, card, 2);
    }

    @Test
    public void movesCardToSideboard() {
        when(storage.moveCardToSideboard(deck, card, 2)).thenReturn(deckCards);
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.moveCardToSideboard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(storage).moveCardToSideboard(deck, card, 2);
    }

    @Test
    public void testImportDeck() throws Throwable {
        when(storage.importDeck(uri)).thenReturn(decks);
        TestObserver<List<Deck>> testSubscriber = new TestObserver<>();
        underTest.importDeck(uri).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(decks);
        verify(storage).importDeck(uri);
    }

    @Test
    public void exportsDeck() {
        when(fileUtil.downloadDeckToSdCard(deck, deckCards)).thenReturn(true);
        TestObserver<Boolean> testSubscriber = new TestObserver<>();
        underTest.exportDeck(deck, deckCards).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(true);
    }

    @Test
    public void throwErrorIfImportFails() throws MTGException {
        MTGException exception = new MTGException(ExceptionCode.DECK_NOT_IMPORTED, "error");
        when(storage.importDeck(uri)).thenThrow(exception);
        TestObserver<List<Deck>> testSubscriber = new TestObserver<>();
        underTest.importDeck(uri).subscribe(testSubscriber);
        testSubscriber.assertError(exception);
    }
}