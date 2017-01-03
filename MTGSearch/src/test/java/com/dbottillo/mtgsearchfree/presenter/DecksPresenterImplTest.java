package com.dbottillo.mtgsearchfree.presenter;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode;
import com.dbottillo.mtgsearchfree.exceptions.MTGException;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.mapper.DeckMapper;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.DecksView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;

import io.reactivex.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DecksPresenterImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private DecksPresenter underTest;

    @Mock
    DecksInteractor interactor;

    @Mock
    DecksView view;

    @Mock
    DeckMapper deckMapper;

    @Mock
    MTGCard card;

    @Mock
    Deck deck;

    @Mock
    Uri uri;

    @Mock
    List<Deck> decks;

    @Mock
    List<MTGCard> cards;

    @Mock
    DeckBucket deckBucket;

    @Mock
    Logger logger;

    @Before
    public void setup() {
        when(interactor.load()).thenReturn(Observable.just(decks));
        when(interactor.addDeck("deck")).thenReturn(Observable.just(decks));
        when(interactor.importDeck(uri)).thenReturn(Observable.just(decks));
        when(interactor.deleteDeck(deck)).thenReturn(Observable.just(decks));
        when(interactor.loadDeck(deck)).thenReturn(Observable.just(cards));
        when(interactor.addCard("new", card, 2)).thenReturn(Observable.just(cards));
        when(interactor.addCard(deck, card, 2)).thenReturn(Observable.just(cards));
        when(interactor.removeCard(deck, card)).thenReturn(Observable.just(cards));
        when(interactor.removeAllCard(deck, card)).thenReturn(Observable.just(cards));
        when(interactor.editDeck(deck, "deck")).thenReturn(Observable.just(cards));
        when(interactor.exportDeck(deck, cards)).thenReturn(Observable.just(true));
        when(interactor.moveCardFromSideboard(deck, card, 2)).thenReturn(Observable.just(cards));
        when(interactor.moveCardToSideboard(deck, card, 2)).thenReturn(Observable.just(cards));
        when(deckMapper.map(cards)).thenReturn(deckBucket);
        underTest = new DecksPresenterImpl(interactor, deckMapper, new TestRunnerFactory(), logger);
        underTest.init(view);
    }

    @Test
    public void testLoadDecks() {
        underTest.loadDecks();
        verify(interactor).load();
        verify(view).decksLoaded(decks);
    }

    @Test
    public void testLoadDeck() {
        underTest.loadDeck(deck);
        verify(interactor).loadDeck(deck);
        verify(view).deckLoaded(deckBucket);
    }

    @Test
    public void testAddDeck() {
        underTest.addDeck("deck");
        verify(interactor).addDeck("deck");
        verify(view).decksLoaded(decks);
    }

    @Test
    public void testDeleteDeck() {
        underTest.deleteDeck(deck);
        verify(interactor).deleteDeck(deck);
        verify(view).decksLoaded(decks);
    }

    @Test
    public void testEditDeck() {
        underTest.editDeck(deck, "deck");
        verify(interactor).editDeck(deck, "deck");
        verify(view).deckLoaded(deckBucket);
    }

    @Test
    public void testAddCardToDeck() {
        underTest.addCardToDeck(deck, card, 2);
        verify(interactor).addCard(deck, card, 2);
        verify(view).deckLoaded(deckBucket);
    }

    @Test
    public void testAddCardToDeckWithName() {
        underTest.addCardToDeck("new", card, 2);
        verify(interactor).addCard("new", card, 2);
        verify(view).deckLoaded(deckBucket);
    }

    @Test
    public void testRemoveCardFromDeck() {
        underTest.removeCardFromDeck(deck, card);
        verify(interactor).removeCard(deck, card);
        verify(view).deckLoaded(deckBucket);
    }

    @Test
    public void testRemoveAllCardFromDeck() {
        underTest.removeAllCardFromDeck(deck, card);
        verify(interactor).removeAllCard(deck, card);
        verify(view).deckLoaded(deckBucket);
    }

    @Test
    public void testImportDeck() {
        underTest.importDeck(uri);
        verify(interactor).importDeck(uri);
        verify(view).decksLoaded(decks);
    }

    @Test
    public void willExportDeck() {
        underTest.exportDeck(deck, cards);
        verify(interactor).exportDeck(deck, cards);
        verify(view).deckExported(true);
    }

    @Test
    public void willShowErrorIfDeckFileCannotBeImported() {
        MTGException exception = new MTGException(ExceptionCode.DECK_NOT_IMPORTED, "error");
        Observable<List<Deck>> observable = Observable.error(exception);
        when(interactor.importDeck(uri)).thenReturn(observable);
        underTest.importDeck(uri);
        verify(interactor).importDeck(uri);
        verify(view).showError(exception);
    }

    @Test
    public void movesCardFromSideboard() {
        underTest.moveCardFromSideBoard(deck, card, 2);
        verify(interactor).moveCardFromSideboard(deck, card, 2);
        verify(view).deckLoaded(deckBucket);
    }

    @Test
    public void movesCardToSideboard() {
        underTest.moveCardToSideBoard(deck, card, 2);
        verify(interactor).moveCardToSideboard(deck, card, 2);
        verify(view).deckLoaded(deckBucket);
    }
}