package com.dbottillo.mtgsearchfree.interactors;

import android.net.Uri;
import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.observers.TestSubscriber;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
@RunWith(RobolectricTestRunner.class)
public class DecksInteractorImplTest {

    static Deck deck;
    static MTGCard card;
    static DecksStorage storage;
    static Uri uri;
    static List<Deck> decks = Arrays.asList(new Deck(2), new Deck(3));
    static List<MTGCard> deckCards = Arrays.asList(new MTGCard(7), new MTGCard(8));

    private DecksInteractor decksInteractor;

    @BeforeClass
    public static void setup(){
        deck = mock(Deck.class);
        card = mock(MTGCard.class);
        storage = mock(DecksStorage.class);
        uri = mock(Uri.class);
        when(storage.load()).thenReturn(decks);
        when(storage.loadDeck(deck)).thenReturn(deckCards);
        when(storage.addDeck("deck")).thenReturn(decks);
        when(storage.deleteDeck(deck)).thenReturn(decks);
        when(storage.editDeck(deck, "new name")).thenReturn(deckCards);
    }
    @Before
    public void init() {
        decksInteractor = new DecksInteractorImpl(storage);
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
        when(storage.addCard(deck, card, 2 )).thenReturn(deckCards);
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        decksInteractor.addCard(deck, card, 2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
        verify(storage).addCard(deck, card, 2);
    }

    @Test
    public void testAddCardWithNewDeck() {
        when(storage.addCard("name", card, 2 )).thenReturn(deckCards);
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
    public void testImportDeck() {
        when(storage.importDeck(uri)).thenReturn(decks);
        TestSubscriber<List<Deck>> testSubscriber = new TestSubscriber<>();
        decksInteractor.importDeck(uri).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(decks));
        verify(storage).importDeck(uri);
    }
}