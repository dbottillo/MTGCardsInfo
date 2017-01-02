package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.mapper.DeckMapper;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.storage.GeneralPreferences;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.CardsView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;

import rx.Observable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CardsPresenterImplTest  {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private CardsPresenter underTest;

    @Mock
    CardsInteractor interactor;

    @Mock
    CardsView view;

    @Mock
    Deck deck;

    @Mock
    MTGCard card;

    @Mock
    MTGSet set;

    @Mock
    List<MTGCard> luckyCards;

    @Mock
    List<MTGCard> deckCards;

    @Mock
    List<MTGCard> favCards;

    @Mock
    List<MTGCard> setCards;

    @Mock
    List<MTGCard> searchCards;

    @Mock
    DeckBucket deckBucket;

    @Mock
    SearchParams searchParams;

    @Mock
    DeckMapper deckMapper;

    @Mock
    Logger logger;

    private int[] idFavs = new int[]{2, 3, 4};

    @Before
    public void setup() {
        MemoryStorage memoryStorage = new MemoryStorage(logger);
        when(deck.getName()).thenReturn("deck");
        when(searchParams.toString()).thenReturn("search");
        when(set.getName()).thenReturn("set");
        when(interactor.getLuckyCards(3)).thenReturn(Observable.just(luckyCards));
        when(interactor.loadDeck(deck)).thenReturn(Observable.just(deckCards));
        when(interactor.getFavourites()).thenReturn(Observable.just(favCards));
        when(interactor.loadIdFav()).thenReturn(Observable.just(idFavs));
        when(interactor.removeFromFavourite(card)).thenReturn(Observable.just(idFavs));
        when(interactor.saveAsFavourite(card)).thenReturn(Observable.just(idFavs));
        when(interactor.doSearch(searchParams)).thenReturn(Observable.just(searchCards));
        when(interactor.loadSet(set)).thenReturn(Observable.just(setCards));
        when(deckMapper.map(deckCards)).thenReturn(deckBucket);
        underTest = new CardsPresenterImpl(interactor, deckMapper, mock(GeneralPreferences.class),
                new TestRunnerFactory(), memoryStorage, logger);
        underTest.init(view);
    }

    @Test
    public void testGetLuckyCards() {
        underTest.getLuckyCards(3);
        verify(interactor).getLuckyCards(3);
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardsLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("lucky"));
        assertThat(argument.getValue().getCards(), is(luckyCards));
    }

    @Test
    public void testLoadFavourites() {
        underTest.loadFavourites();
        verify(interactor).getFavourites();
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardsLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("fav"));
        assertThat(argument.getValue().getCards(), is(favCards));
    }

    @Test
    public void testLoadDeck() {
        underTest.loadDeck(deck);
        verify(interactor).loadDeck(deck);
        ArgumentCaptor<DeckBucket> argument = ArgumentCaptor.forClass(DeckBucket.class);
        verify(view).deckLoaded(argument.capture());
        assertThat(argument.getValue(), is(deckBucket));
    }

    @Test
    public void testDoSearch() {
        underTest.doSearch(searchParams);
        verify(interactor).doSearch(searchParams);
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardsLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("search"));
        assertThat(argument.getValue().getCards(), is(searchCards));
    }

    @Test
    public void testRemoveFromFavourite() {
        underTest.removeFromFavourite(card, false);
        verifyNoMoreInteractions(view);
        CardsView newView = mock(CardsView.class);
        underTest.init(newView);
        underTest.removeFromFavourite(card, true);
        verify(newView).favIdLoaded(idFavs);
        verify(interactor, times(2)).removeFromFavourite(card);
    }

    @Test
    public void testSaveAsFavourite() {
        underTest.saveAsFavourite(card, false);
        verifyNoMoreInteractions(view);
        CardsView newView = mock(CardsView.class);
        underTest.init(newView);
        underTest.saveAsFavourite(card, true);
        verify(newView).favIdLoaded(idFavs);
        verify(interactor, times(2)).saveAsFavourite(card);
    }

    @Test
    public void testLoadCards() {
        underTest.loadCards(set);
        verify(interactor).loadSet(set);
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardsLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("set"));
        assertThat(argument.getValue().getCards(), is(setCards));
    }

    @Test
    public void loadIDFavsFromDatabaseOnColdStart() {
        underTest.loadIdFavourites();
        verify(interactor).loadIdFav();
        verify(view).favIdLoaded(idFavs);
    }

    @Test
    public void keepIDFavsInCacheAfterFirstRun() throws InterruptedException {
        underTest.loadIdFavourites();
        underTest.loadIdFavourites();
        verify(interactor, times(1)).loadIdFav();
        verify(view, times(2)).favIdLoaded(idFavs);
    }


}