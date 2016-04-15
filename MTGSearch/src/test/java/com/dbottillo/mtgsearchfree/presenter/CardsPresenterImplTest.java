package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.view.CardFilterView;
import com.dbottillo.mtgsearchfree.view.CardsView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardsPresenterImplTest extends BaseTest {

    CardsPresenter presenter;

    CardsInteractor interactor;

    CardsView view;

    @Mock
    Deck deck;

    @Mock
    List<MTGCard> luckyCards;

    @Mock
    List<MTGCard> deckCards;

    @Before
    public void setup() {
        setupRxJava();
        MockitoAnnotations.initMocks(this);
        interactor = mock(CardsInteractor.class);
        view = mock(CardsView.class);
        when(deck.getName()).thenReturn("deck");
        when(interactor.getLuckyCards(3)).thenReturn(Observable.just(luckyCards));
        when(interactor.loadDeck(deck)).thenReturn(Observable.just(deckCards));
        presenter = new CardsPresenterImpl(interactor);
        presenter.init(view);
    }

    @Test
    public void testGetLuckyCards() {
        presenter.getLuckyCards(3);
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("lucky"));
        assertThat(argument.getValue().getCards(), is(luckyCards));
    }

    @Test
    public void testLoadFavourites() {

    }

    @Test
    public void testLoadDeck() {
        presenter.loadDeck(deck);
        ArgumentCaptor<CardsBucket> argument = ArgumentCaptor.forClass(CardsBucket.class);
        verify(view).cardLoaded(argument.capture());
        assertThat(argument.getValue().getKey(), is("deck"));
        assertThat(argument.getValue().getCards(), is(deckCards));
    }

    @Test
    public void testDoSearch() {

    }

    @Test
    public void testRemoveFromFavourite() {

    }

    @Test
    public void testSaveAsFavourite() {

    }

    @Test
    public void testLoadCards() {

    }

    @Test
    public void testLoadIdFavourites() {

    }
}