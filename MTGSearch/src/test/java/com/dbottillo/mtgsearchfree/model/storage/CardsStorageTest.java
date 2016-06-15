package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardsStorageTest extends BaseTest {

    static MTGSet set;
    static Deck deck;
    static MTGCardDataSource mtgCardDataSource;
    static CardsInfoDbHelper cardsInfoDbHelper;
    static List<MTGCard> setCards = Arrays.asList(new MTGCard(5), new MTGCard(6));
    static List<MTGCard> luckyCards = Arrays.asList(new MTGCard(8), new MTGCard(9));
    static List<MTGCard> deckCards = Arrays.asList(new MTGCard(18), new MTGCard(19));
    static List<MTGCard> searchCards = Arrays.asList(new MTGCard(12), new MTGCard(13));
    static List<MTGCard> favCards;

    CardsStorage cardsStorage;

    @BeforeClass
    public static void staticSetup() {
        set = mock(MTGSet.class);
        deck = mock(Deck.class);
        MTGCard fav1 = new MTGCard(7);
        fav1.setMultiVerseId(100);
        MTGCard fav2 = new MTGCard(8);
        fav1.setMultiVerseId(101);
        favCards = Arrays.asList(fav1, fav2);
    }

    @Before
    public void setupStorage() {
        mtgCardDataSource = mock(MTGCardDataSource.class);
        cardsInfoDbHelper = mock(CardsInfoDbHelper.class);
        when(mtgCardDataSource.getSet(set)).thenReturn(setCards);
        when(cardsInfoDbHelper.loadFav(anyBoolean())).thenReturn(favCards);
        when(mtgCardDataSource.getRandomCard(2)).thenReturn(luckyCards);
        when(mtgCardDataSource.searchCards(Matchers.any(SearchParams.class))).thenReturn(searchCards);
        when(cardsInfoDbHelper.loadDeck(deck)).thenReturn(deckCards);
        cardsStorage = new CardsStorage(mtgCardDataSource, cardsInfoDbHelper);
    }

    @Test
    public void testLoad() {
        List<MTGCard> cards = cardsStorage.load(set);
        assertThat(cards.size(), is(2));
        assertThat(cards.get(0).getId(), is(5L));
        assertThat(cards.get(1).getId(), is(6L));
    }

    @Test
    public void testSaveAsFavourite() {
        MTGCard card = mock(MTGCard.class);
        int[] favs = cardsStorage.saveAsFavourite(card);
        verify(cardsInfoDbHelper).saveFavourite(card);
        assertThat(favs.length, is(2));
    }

    @Test
    public void testLoadIdFav() {
        int[] favs = cardsStorage.loadIdFav();
        verify(cardsInfoDbHelper).loadFav(false);
        assertThat(favs.length, is(2));
        assertThat(favs[0], is(favCards.get(0).getMultiVerseId()));
        assertThat(favs[1], is(favCards.get(1).getMultiVerseId()));
    }

    @Test
    public void testRemoveFromFavourite() {
        MTGCard card = mock(MTGCard.class);
        int[] favs = cardsStorage.removeFromFavourite(card);
        verify(cardsInfoDbHelper).removeFavourite(card);
        verify(cardsInfoDbHelper).loadFav(false);
        assertThat(favs.length, is(2));
    }

    @Test
    public void testGetLuckyCards() {
        List<MTGCard> lucky = cardsStorage.getLuckyCards(2);
        verify(mtgCardDataSource).getRandomCard(2);
        assertThat(lucky.size(), is(2));
        assertThat(lucky, is(luckyCards));
    }

    @Test
    public void testGetFavourites() {
        List<MTGCard> favs = cardsStorage.getFavourites();
        verify(cardsInfoDbHelper).loadFav(true);
        assertNotNull(favs);
        assertThat(favs, is(favCards));
    }

    @Test
    public void testLoadDeck() {
        List<MTGCard> cards = cardsStorage.loadDeck(deck);
        verify(cardsInfoDbHelper).loadDeck(deck);
        assertNotNull(cards);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testDoSearch() {
        SearchParams searchParams = mock(SearchParams.class);
        List<MTGCard> search = cardsStorage.doSearch(searchParams);
        verify(mtgCardDataSource).searchCards(searchParams);
        assertNotNull(search);
        assertThat(search, is(searchCards));
    }
}