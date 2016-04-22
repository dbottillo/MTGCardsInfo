package com.dbottillo.mtgsearchfree.presenter;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.view.CardsView;
import com.dbottillo.mtgsearchfree.view.DecksView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
public class DecksPresenterImplTest extends BaseTest {

    DecksPresenter presenter;

    DecksInteractor interactor;

    DecksView view;

    @Mock
    MTGCard card;

    @Mock
    Deck deck;

    @Mock
    List<Deck> decks;

    @Mock
    List<MTGCard> cards;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        interactor = mock(DecksInteractor.class);
        view = mock(DecksView.class);
        when(interactor.load()).thenReturn(Observable.just(decks));
        when(interactor.addDeck("deck")).thenReturn(Observable.just(decks));
        when(interactor.deleteDeck(deck)).thenReturn(Observable.just(decks));
        when(interactor.loadDeck(deck)).thenReturn(Observable.just(cards));
        when(interactor.addCard("new", card, 2)).thenReturn(Observable.just(cards));
        when(interactor.addCard(deck, card, 2)).thenReturn(Observable.just(cards));
        when(interactor.removeCard(deck, card)).thenReturn(Observable.just(cards));
        when(interactor.removeAllCard(deck, card)).thenReturn(Observable.just(cards));
        when(interactor.editDeck(deck, "deck")).thenReturn(Observable.just(cards));
        presenter = new DecksPresenterImpl(interactor);
        presenter.init(view);
    }

    @Test
    public void testLoadDecks() {
        presenter.loadDecks();
        verify(interactor).load();
        verify(view).decksLoaded(decks);
    }

    @Test
    public void testLoadDeck() {
        presenter.loadDeck(deck);
        verify(interactor).loadDeck(deck);
        verify(view).deckLoaded(cards);
    }

    @Test
    public void testAddDeck() {
        presenter.addDeck("deck");
        verify(interactor).addDeck("deck");
        verify(view).decksLoaded(decks);
    }

    @Test
    public void testDeleteDeck() {
        presenter.deleteDeck(deck);
        verify(interactor).deleteDeck(deck);
        verify(view).decksLoaded(decks);
    }

    @Test
    public void testEditDeck() {
        presenter.editDeck(deck, "deck");
        verify(interactor).editDeck(deck, "deck");
        verify(view).deckLoaded(cards);
    }

    @Test
    public void testAddCardToDeck() {
        presenter.addCardToDeck(deck, card, 2);
        verify(interactor).addCard(deck, card, 2);
        verify(view).deckLoaded(cards);
    }

    @Test
    public void testAddCardToDeckWithName() {
        presenter.addCardToDeck("new", card, 2);
        verify(interactor).addCard("new", card, 2);
        verify(view).deckLoaded(cards);
    }

    @Test
    public void testRemoveCardFromDeck() {
        presenter.removeCardFromDeck(deck, card);
        verify(interactor).removeCard(deck, card);
        verify(view).deckLoaded(cards);
    }

    @Test
    public void testRemoveAllCardFromDeck() {
        presenter.removeAllCardFromDeck(deck, card);
        verify(interactor).removeAllCard(deck, card);
        verify(view).deckLoaded(cards);
    }
}