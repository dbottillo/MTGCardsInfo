package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorageImpl;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardsInteractorImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final int MULTIVERSE_ID = 180607;
    private CardsInteractor underTest;

    @Mock
    private CardsStorageImpl cardsStorageImpl;
    @Mock
    private MTGSet set;
    @Mock
    private SearchParams searchParams;
    @Mock
    private Deck deck;
    @Mock
    private MTGCard card;
    @Mock
    private MTGCard otherSideCard;
    @Mock
    Logger logger;

    private List<MTGCard> luckyCards = Arrays.asList(new MTGCard(2), new MTGCard(3));
    private List<MTGCard> favCards = Arrays.asList(new MTGCard(3), new MTGCard(4));
    private List<MTGCard> setCards = Arrays.asList(new MTGCard(5), new MTGCard(6));
    private List<MTGCard> searchCards = Arrays.asList(new MTGCard(6), new MTGCard(7));
    private List<MTGCard> deckCards = Arrays.asList(new MTGCard(7), new MTGCard(8));

    @Before
    public void setup() {
        when(cardsStorageImpl.getLuckyCards(2)).thenReturn(luckyCards);
        when(cardsStorageImpl.getFavourites()).thenReturn(favCards);
       // when(cardsStorageImpl.load(set)).thenReturn(setCards);
        when(cardsStorageImpl.doSearch(searchParams)).thenReturn(searchCards);
        when(cardsStorageImpl.loadDeck(deck)).thenReturn(deckCards);
        when(cardsStorageImpl.loadCard(MULTIVERSE_ID)).thenReturn(card);
        when(cardsStorageImpl.loadOtherSide(card)).thenReturn(otherSideCard);
        underTest = new CardsInteractorImpl(cardsStorageImpl, logger);
    }

    @Test
    public void testGetLuckyCards() {
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.getLuckyCards(2).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(luckyCards);
        verify(cardsStorageImpl).getLuckyCards(2);
    }

    @Test
    public void testGetFavourites() {
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.getFavourites().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(favCards);
        verify(cardsStorageImpl).getFavourites();
    }

    @Test
    public void testSaveAsFavourite() {
        MTGCard card = mock(MTGCard.class);
        int[] idFavs = new int[]{1, 2, 3};
        when(cardsStorageImpl.saveAsFavourite(card)).thenReturn(idFavs);
        TestObserver<int[]> testSubscriber = new TestObserver<>();
        underTest.saveAsFavourite(card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(idFavs);
        verify(cardsStorageImpl).saveAsFavourite(card);
    }

    @Test
    public void testRemoveFromFavourite() {
        MTGCard card = mock(MTGCard.class);
        int[] idFavs = new int[]{3, 4, 5};
        when(cardsStorageImpl.removeFromFavourite(card)).thenReturn(idFavs);
        TestObserver<int[]> testSubscriber = new TestObserver<>();
        underTest.removeFromFavourite(card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(idFavs);
        verify(cardsStorageImpl).removeFromFavourite(card);
    }

   /* @Test
    public void testLoadSet() {
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.loadSet(set).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(setCards);
        verify(cardsStorageImpl).load(set);
    }*/

    @Test
    public void testLoadIdFav() {
        int[] idFavs = new int[]{6, 7, 8};
        when(cardsStorageImpl.loadIdFav()).thenReturn(idFavs);
        TestObserver<int[]> testSubscriber = new TestObserver<>();
        underTest.loadIdFav().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(idFavs);
        verify(cardsStorageImpl).loadIdFav();
    }

    @Test
    public void testLoadDeck() {
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.loadDeck(deck).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(deckCards);
        verify(cardsStorageImpl).loadDeck(deck);
    }

    @Test
    public void testDoSearch() {
        TestObserver<List<MTGCard>> testSubscriber = new TestObserver<>();
        underTest.doSearch(searchParams).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(searchCards);
        verify(cardsStorageImpl).doSearch(searchParams);
    }

    @Test
    public void testLoadCardWithMultiverseId() {
        TestObserver<MTGCard> testSubscriber = new TestObserver<>();
        underTest.loadCard(MULTIVERSE_ID).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(card);
        verify(cardsStorageImpl).loadCard(MULTIVERSE_ID);
    }

    @Test
    public void testLoadOtherSideCard() {
        TestObserver<MTGCard> testSubscriber = new TestObserver<>();
        underTest.loadOtherSideCard(card).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(otherSideCard);
        verify(cardsStorageImpl).loadOtherSide(card);
    }
}