package com.dbottillo.mtgsearchfree.interactors;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;

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
import static org.mockito.Mockito.when;

@SmallTest
@RunWith(RobolectricTestRunner.class)
public class CardsInteractorImplTest {

    static CardsStorage cardsStorage;
    CardsInteractor cardsInteractor;

    static MTGSet set;
    static SearchParams searchParams;
    static Deck deck;
    static List<MTGCard> luckyCards = Arrays.asList(new MTGCard(2), new MTGCard(3));
    static List<MTGCard> favCards = Arrays.asList(new MTGCard(3), new MTGCard(4));
    static List<MTGCard> setCards = Arrays.asList(new MTGCard(5), new MTGCard(6));
    static List<MTGCard> searchCards = Arrays.asList(new MTGCard(6), new MTGCard(7));
    static List<MTGCard> deckCards = Arrays.asList(new MTGCard(7), new MTGCard(8));

    @BeforeClass
    public static void setupStorage() {
        set = mock(MTGSet.class);
        deck = mock(Deck.class);
        searchParams = mock(SearchParams.class);
        cardsStorage = mock(CardsStorage.class);
        when(cardsStorage.getLuckyCards(2)).thenReturn(luckyCards);
        when(cardsStorage.getFavourites()).thenReturn(favCards);
        when(cardsStorage.load(set)).thenReturn(setCards);
        when(cardsStorage.doSearch(searchParams)).thenReturn(searchCards);
        when(cardsStorage.loadDeck(deck)).thenReturn(deckCards);
    }

    @Before
    public void setup() {
        cardsInteractor = new CardsInteractorImpl(cardsStorage);
    }

    @Test
    public void testGetLuckyCards() {
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        cardsInteractor.getLuckyCards(2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(luckyCards));
    }

    @Test
    public void testGetFavourites() {
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        cardsInteractor.getFavourites().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(favCards));
    }

    @Test
    public void testSaveAsFavourite() {
        MTGCard card = mock(MTGCard.class);
        int[] idFavs = new int[]{1, 2, 3};
        when(cardsStorage.saveAsFavourite(card)).thenReturn(idFavs);
        TestSubscriber<int[]> testSubscriber = new TestSubscriber<>();
        cardsInteractor.saveAsFavourite(card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(idFavs));
    }

    @Test
    public void testRemoveFromFavourite() {
        MTGCard card = mock(MTGCard.class);
        int[] idFavs = new int[]{3, 4, 5};
        when(cardsStorage.removeFromFavourite(card)).thenReturn(idFavs);
        TestSubscriber<int[]> testSubscriber = new TestSubscriber<>();
        cardsInteractor.removeFromFavourite(card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(idFavs));
    }

    @Test
    public void testLoadSet() {
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        cardsInteractor.loadSet(set).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(setCards));
    }

    @Test
    public void testLoadIdFav() {
        int[] idFavs = new int[]{6, 7, 8};
        when(cardsStorage.loadIdFav()).thenReturn(idFavs);
        TestSubscriber<int[]> testSubscriber = new TestSubscriber<>();
        cardsInteractor.loadIdFav().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(idFavs));
    }

    @Test
    public void testLoadDeck() {
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        cardsInteractor.loadDeck(deck).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(deckCards));
    }

    @Test
    public void testDoSearch() {
        TestSubscriber<List<MTGCard>> testSubscriber = new TestSubscriber<>();
        cardsInteractor.doSearch(searchParams).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(searchCards));
    }
}