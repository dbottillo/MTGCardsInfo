package com.dbottillo.mtgsearchfree.interactors;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode;
import com.dbottillo.mtgsearchfree.exceptions.MTGException;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.util.FileUtil;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class DecksInteractorImplTest {

    private static Deck deck;
    private static MTGCard card;
    private static DecksStorage storage;
    private static FileUtil fileUtil;
    private static Uri uri;
    private static List<Deck> decks = Arrays.asList(new Deck(2), new Deck(3));
    private static List<MTGCard> deckCards = Arrays.asList(new MTGCard(7), new MTGCard(8));

    private DecksInteractor decksInteractor;

    @BeforeClass
    public static void setup() {

    }

    @Before
    public void init() {
        deck = mock(Deck.class);
        card = mock(MTGCard.class);
        storage = mock(DecksStorage.class);
        fileUtil = mock(FileUtil.class);
        uri = mock(Uri.class);
        when(storage.load()).thenReturn(decks);
        when(storage.loadDeck(deck)).thenReturn(deckCards);
        when(storage.addDeck("deck")).thenReturn(decks);
        when(storage.deleteDeck(deck)).thenReturn(decks);
        when(storage.editDeck(deck, "new name")).thenReturn(deckCards);
        decksInteractor = new DecksInteractorImpl(storage, fileUtil);
    }


    @Test
    public void testLoad() {
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        decksInteractor.load().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(decks));
    }

    @Test
    public void testLoadDeck() {
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        decksInteractor.loadDeck(deck).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).loadDeck(deck);
    }

    @Test
    public void testAddDeck() {
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        decksInteractor.addDeck("deck").subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(decks));
        verify(storage).addDeck("deck");
    }

    @Test
    public void testDeleteDeck() {
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        decksInteractor.deleteDeck(deck).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(decks));
        verify(storage).deleteDeck(deck);
    }

    @Test
    public void testEditDeck() {
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        decksInteractor.editDeck(deck, "new name").subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).editDeck(deck, "new name");
    }

    @Test
    public void testAddCard() {
        when(storage.addCard(deck, card, 2)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        decksInteractor.addCard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).addCard(deck, card, 2);
    }

    @Test
    public void testAddCardWithNewDeck() {
        when(storage.addCard("name", card, 2)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        decksInteractor.addCard("name", card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).addCard("name", card, 2);
    }

    @Test
    public void testRemoveCard() {
        when(storage.removeCard(deck, card)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        decksInteractor.removeCard(deck, card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).removeCard(deck, card);
    }

    @Test
    public void testRemoveAllCard() {
        when(storage.removeAllCard(deck, card)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        decksInteractor.removeAllCard(deck, card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).removeAllCard(deck, card);
    }

    @Test
    public void movesCardFromSideboard() {
        when(storage.moveCardFromSideboard(deck, card, 2)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        decksInteractor.moveCardFromSideboard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).moveCardFromSideboard(deck, card, 2);
    }

    @Test
    public void movesCardToSideboard() {
        when(storage.moveCardToSideboard(deck, card, 2)).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        decksInteractor.moveCardToSideboard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).moveCardToSideboard(deck, card, 2);
    }

    @Test
    public void testImportDeck() throws Throwable {
        when(storage.importDeck(uri)).thenReturn(decks);
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        decksInteractor.importDeck(uri).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(decks));
        verify(storage).importDeck(uri);
    }

    @Test
    public void exportsDeck() {
        when(fileUtil.downloadDeckToSdCard(deck, deckCards)).thenReturn(true);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        decksInteractor.exportDeck(deck, deckCards).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(true));
    }

    @Test
    public void throwErrorIfImportFails() throws MTGException {
        MTGException exception = new MTGException(ExceptionCode.DECK_NOT_IMPORTED, "error");
        when(storage.importDeck(uri)).thenThrow(exception);
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        decksInteractor.importDeck(uri).subscribe(testSubscriber);
        testSubscriber.assertError(exception);
    }
}