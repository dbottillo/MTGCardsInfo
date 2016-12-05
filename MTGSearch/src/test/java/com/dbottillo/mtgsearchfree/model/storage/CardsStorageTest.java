package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.model.database.FavouritesDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.util.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CardsStorageTest {

    private static final int MULTIVERSE_ID = 180607;

    @Mock
    private static MTGSet set;
    @Mock
    private Deck deck;

    @Mock
    private MTGCard card;

    @Mock
    private MTGCard mainSideCard;

    @Mock
    private MTGCard secondSideCard;

    @Mock
    private Logger logger;

    private MTGCardDataSource mtgCardDataSource;
    private DeckDataSource deckDataSource;
    private FavouritesDataSource favouritesDataSource;
    private List<MTGCard> setCards = Arrays.asList(new MTGCard(5), new MTGCard(6));
    private List<MTGCard> luckyCards = Arrays.asList(new MTGCard(8), new MTGCard(9));
    private List<MTGCard> deckCards = Arrays.asList(new MTGCard(18), new MTGCard(19));
    private List<MTGCard> searchCards = Arrays.asList(new MTGCard(12), new MTGCard(13));
    private List<MTGCard> favCards;

    private CardsStorage underTest;

    @Before
    public void setupStorage() {
        MTGCard fav1 = new MTGCard(7);
        fav1.setMultiVerseId(100);
        MTGCard fav2 = new MTGCard(8);
        fav1.setMultiVerseId(101);
        favCards = Arrays.asList(fav1, fav2);
        mtgCardDataSource = mock(MTGCardDataSource.class);
        deckDataSource = mock(DeckDataSource.class);
        favouritesDataSource = mock(FavouritesDataSource.class);
        when(mtgCardDataSource.getSet(set)).thenReturn(setCards);
        when(favouritesDataSource.getCards(anyBoolean())).thenReturn(favCards);
        when(mtgCardDataSource.getRandomCard(2)).thenReturn(luckyCards);
        when(mtgCardDataSource.searchCards(Matchers.any(SearchParams.class))).thenReturn(searchCards);
        when(deckDataSource.getCards(deck)).thenReturn(deckCards);
        when(mainSideCard.getName()).thenReturn("One");
        when(secondSideCard.getName()).thenReturn("Two");
        when(mtgCardDataSource.searchCard("Two")).thenReturn(secondSideCard);
        when(mtgCardDataSource.searchCard("One")).thenReturn(mainSideCard);
        when(mainSideCard.getNames()).thenReturn(Arrays.asList("One", "Two"));
        when(secondSideCard.getNames()).thenReturn(Arrays.asList("One", "Two"));
        underTest = new CardsStorage(mtgCardDataSource, deckDataSource, favouritesDataSource, logger);
    }

    @Test
    public void testLoad() {
        List<MTGCard> cards = underTest.load(set);
        assertThat(cards.size(), is(2));
        assertThat(cards.get(0).getId(), is(5L));
        assertThat(cards.get(1).getId(), is(6L));
    }

    @Test
    public void testSaveAsFavourite() {
        MTGCard card = mock(MTGCard.class);
        int[] favs = underTest.saveAsFavourite(card);
        verify(favouritesDataSource).saveFavourites(card);
        assertThat(favs.length, is(2));
    }

    @Test
    public void testLoadIdFav() {
        int[] favs = underTest.loadIdFav();
        verify(favouritesDataSource).getCards(false);
        assertThat(favs.length, is(2));
        assertThat(favs[0], is(favCards.get(0).getMultiVerseId()));
        assertThat(favs[1], is(favCards.get(1).getMultiVerseId()));
    }

    @Test
    public void testRemoveFromFavourite() {
        MTGCard card = mock(MTGCard.class);
        int[] favs = underTest.removeFromFavourite(card);
        verify(favouritesDataSource).removeFavourites(card);
        verify(favouritesDataSource).getCards(false);
        assertThat(favs.length, is(2));
    }

    @Test
    public void testGetLuckyCards() {
        List<MTGCard> lucky = underTest.getLuckyCards(2);
        verify(mtgCardDataSource).getRandomCard(2);
        assertThat(lucky.size(), is(2));
        assertThat(lucky, is(luckyCards));
    }

    @Test
    public void testGetFavourites() {
        List<MTGCard> favs = underTest.getFavourites();
        verify(favouritesDataSource).getCards(true);
        assertNotNull(favs);
        assertThat(favs, is(favCards));
    }

    @Test
    public void testLoadDeck() {
        List<MTGCard> cards = underTest.loadDeck(deck);
        verify(deckDataSource).getCards(deck);
        assertNotNull(cards);
        assertThat(cards, is(deckCards));
    }

    @Test
    public void testDoSearch() {
        SearchParams searchParams = mock(SearchParams.class);
        List<MTGCard> search = underTest.doSearch(searchParams);
        verify(mtgCardDataSource).searchCards(searchParams);
        assertNotNull(search);
        assertThat(search, is(searchCards));
    }

    @Test
    public void testShouldRetrieveCardsByMultiverseId() {
        when(mtgCardDataSource.searchCard(MULTIVERSE_ID)).thenReturn(card);

        MTGCard result = underTest.loadCard(MULTIVERSE_ID);

        assertThat(result, is(card));
        verify(mtgCardDataSource).searchCard(MULTIVERSE_ID);
        verifyNoMoreInteractions(mtgCardDataSource);
    }

    @Test
    public void shouldRetrieveOtherSideCards() throws Exception {
        MTGCard result = underTest.loadOtherSide(mainSideCard);
        assertThat(result, is(secondSideCard));

        result = underTest.loadOtherSide(secondSideCard);
        assertThat(result, is(mainSideCard));
    }

    @Test
    public void loadOtherSide_shouldReturnTheSameCard_IfCardIsNotDouble() throws Exception {
        when(mainSideCard.getNames()).thenReturn(null);
        MTGCard result = underTest.loadOtherSide(mainSideCard);
        assertThat(result, is(mainSideCard));

        when(mainSideCard.getNames()).thenReturn(Arrays.asList("One"));
        MTGCard result2 = underTest.loadOtherSide(mainSideCard);
        assertThat(result2, is(mainSideCard));
    }
}