package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.view.CardFilterView;
import com.dbottillo.mtgsearchfree.view.CardsView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

import java.util.List;

import rx.Observable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CardsPresenterImplTest extends BaseTest {

    CardsPresenter presenter;

    CardsInteractor interactor;

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

    int[] idFavs;

    @Mock
    List<MTGCard> searchCards;

    @Mock
    SearchParams searchParams;

    @Before
    public void setup() {
        setupRxJava();
        MockitoAnnotations.initMocks(this);
        interactor = mock(CardsInteractor.class);
        view = mock(CardsView.class);
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
        presenter = new CardsPresenterImpl(interactor);
        presenter.init(view);
    }

    @Test
    public void testGetLuckyCards() {
        presenter.getLuckyCards(3);
        sync();
        verify(interactor).getLuckyCards(3);
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("lucky"));
        assertThat(argument.getValue().getCards(), is(luckyCards));
    }

    @Test
    public void testLoadFavourites() {
        presenter.loadFavourites();
        sync();
        verify(interactor).getFavourites();
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("fav"));
        assertThat(argument.getValue().getCards(), is(favCards));
    }

    @Test
    public void testLoadDeck() {
        presenter.loadDeck(deck);
        sync();
        verify(interactor).loadDeck(deck);
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("deck"));
        assertThat(argument.getValue().getCards(), is(deckCards));
    }

    @Test
    public void testDoSearch() {
        presenter.doSearch(searchParams);
        sync();
        verify(interactor).doSearch(searchParams);
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("search"));
        assertThat(argument.getValue().getCards(), is(searchCards));
    }

    @Test
    public void testRemoveFromFavourite() {
        presenter.removeFromFavourite(card, false);
        sync();
        verifyNoMoreInteractions(view);
        CardsView newView = mock(CardsView.class);
        presenter.init(newView);
        presenter.removeFromFavourite(card, true);
        sync();
        verify(newView).favIdLoaded(idFavs);
        verify(interactor, times(2)).removeFromFavourite(card);
    }

    @Test
    public void testSaveAsFavourite() {
        presenter.saveAsFavourite(card, false);
        sync();
        verifyNoMoreInteractions(view);
        CardsView newView = mock(CardsView.class);
        presenter.init(newView);
        presenter.saveAsFavourite(card, true);
        sync();
        verify(newView).favIdLoaded(idFavs);
        verify(interactor, times(2)).saveAsFavourite(card);
    }

    @Test
    public void removeFavsInvalidateFavCache(){
        presenter.loadFavourites();
        sync();
        assertNotNull(CardsMemoryStorage.bucket);
        presenter.removeFromFavourite(card, false);
        sync();
        assertNull(CardsMemoryStorage.bucket);
    }

    @Test
    public void saveFavsInvalidateFavCache(){
        presenter.loadFavourites();
        sync();
        assertNotNull(CardsMemoryStorage.bucket);
        presenter.saveAsFavourite(card, false);
        sync();
        assertNull(CardsMemoryStorage.bucket);
    }

    @Test
    public void changeFavsNotInvalidateNonFavCache(){
        presenter.doSearch(searchParams);
        sync();
        assertNotNull(CardsMemoryStorage.bucket);
        presenter.saveAsFavourite(card, false);
        sync();
        assertNotNull(CardsMemoryStorage.bucket);
    }

    @Test
    public void testLoadCards() {
        presenter.loadCards(set);
        sync();
        verify(interactor).loadSet(set);
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("set"));
        assertThat(argument.getValue().getCards(), is(setCards));
    }

    @Test
    public void testLoadIdFavourites() {
        presenter.loadIdFavourites();
        sync();
        verify(interactor).loadIdFav();
        verify(view).favIdLoaded(idFavs);
    }
}