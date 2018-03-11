package com.dbottillo.mtgsearchfree.ui.decks

import android.os.Bundle
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.decks.deck.DECK_KEY
import com.dbottillo.mtgsearchfree.ui.decks.deck.DeckPresenter
import com.dbottillo.mtgsearchfree.ui.decks.deck.DeckView
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class DeckPresenterTest {

    @Rule
    @JvmField
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var interactor: DecksInteractor
    @Mock
    internal lateinit var logger: Logger
    @Mock lateinit var view: DeckView
    @Mock lateinit var deck: Deck
    @Mock lateinit var bundle: Bundle
    @Mock lateinit var card: MTGCard
    @Mock lateinit var cards: DeckCollection

    lateinit var underTest: DeckPresenter

    @Before
    fun setup() {
        `when`(bundle.get(DECK_KEY)).thenReturn(deck)
        `when`(deck.name).thenReturn("name")
        `when`(cards.numberOfCardsWithoutSideboard()).thenReturn(60)
        `when`(cards.numberOfCardsInSideboard()).thenReturn(15)
        underTest = DeckPresenter(interactor, logger)
        underTest.init(view, bundle)
    }

    @Test
    fun `load deck, should call interactor and update view`() {
        `when`(interactor.loadDeck(deck)).thenReturn(Observable.just(cards))

        underTest.loadDeck()

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).loadDeck(deck)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `add card to deck, should call interactor and update view`() {
        `when`(interactor.addCard(deck, card, 6)).thenReturn(Observable.just(cards))

        underTest.addCardToDeck(card, 6)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).addCard(deck, card, 6)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `remove card from deck, should call interactor and update view`() {
        `when`(interactor.removeCard(deck, card)).thenReturn(Observable.just(cards))

        underTest.removeCardFromDeck(card)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).removeCard(deck, card)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `remove all cards from deck, should call interactor and update view`() {
        `when`(interactor.removeAllCard(deck, card)).thenReturn(Observable.just(cards))

        underTest.removeAllCardFromDeck(card)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).removeAllCard(deck, card)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `move card from sideboard, should call interactor and update view`() {
        `when`(interactor.moveCardFromSideboard(deck, card, 6)).thenReturn(Observable.just(cards))

        underTest.moveCardFromSideBoard(card, 6)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).moveCardFromSideboard(deck, card, 6)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `move card to sideboard, should call interactor and update view`() {
        `when`(interactor.moveCardToSideboard(deck, card, 6)).thenReturn(Observable.just(cards))

        underTest.moveCardToSideBoard(card, 6)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).moveCardToSideboard(deck, card, 6)
        verifyNoMoreInteractions(view, interactor)
    }

}