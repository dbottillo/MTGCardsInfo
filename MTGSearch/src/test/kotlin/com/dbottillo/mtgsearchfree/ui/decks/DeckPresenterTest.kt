package com.dbottillo.mtgsearchfree.ui.decks

import android.os.Bundle
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.decks.deck.DeckPresenter
import com.dbottillo.mtgsearchfree.ui.decks.deck.DeckView
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit

class DeckPresenterTest {

    @Rule @JvmField val mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var interactor: DecksInteractor
    @Mock lateinit var view: DeckView
    @Mock lateinit var deck: Deck
    @Mock lateinit var bundle: Bundle
    @Mock lateinit var card: MTGCard
    @Mock lateinit var cards: DeckCollection

    lateinit var underTest: DeckPresenter

    @Before
    fun setup() {
        whenever(deck.id).thenReturn(2L)
        whenever(deck.name).thenReturn("name")
        whenever(cards.numberOfCardsWithoutSideboard()).thenReturn(60)
        whenever(cards.numberOfCardsInSideboard()).thenReturn(15)
        whenever(interactor.loadDeckById(2L)).thenReturn(Single.just(deck))
        whenever(interactor.loadDeck(2L)).thenReturn(Observable.just(cards))
        underTest = DeckPresenter(interactor)
    }

    @Test
    fun `load deck, should call interactor and update view`() {
        underTest.init(view, 2L)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).loadDeck(2L)
        verify(interactor).loadDeckById(2L)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `add card to deck, should call interactor and update view`() {
        underTest.init(view, 2L)
        Mockito.reset(view, interactor)
        whenever(interactor.addCard(deck, card, 6)).thenReturn(Observable.just(cards))

        underTest.addCardToDeck(card, 6)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).addCard(deck, card, 6)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `remove card from deck, should call interactor and update view`() {
        underTest.init(view, 2L)
        Mockito.reset(view, interactor)
        whenever(interactor.removeCard(deck, card)).thenReturn(Observable.just(cards))

        underTest.removeCardFromDeck(card)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).removeCard(deck, card)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `remove all cards from deck, should call interactor and update view`() {
        underTest.init(view, 2L)
        Mockito.reset(view, interactor)
        whenever(interactor.removeAllCard(deck, card)).thenReturn(Observable.just(cards))

        underTest.removeAllCardFromDeck(card)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).removeAllCard(deck, card)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `move card from sideboard, should call interactor and update view`() {
        underTest.init(view, 2L)
        Mockito.reset(view, interactor)
        whenever(interactor.moveCardFromSideboard(deck, card, 6)).thenReturn(Observable.just(cards))

        underTest.moveCardFromSideBoard(card, 6)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).moveCardFromSideboard(deck, card, 6)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `move card to sideboard, should call interactor and update view`() {
        underTest.init(view, 2L)
        Mockito.reset(view, interactor)
        whenever(interactor.moveCardToSideboard(deck, card, 6)).thenReturn(Observable.just(cards))

        underTest.moveCardToSideBoard(card, 6)

        verify(view).deckLoaded("name (60/15)", cards)
        verify(interactor).moveCardToSideboard(deck, card, 6)
        verifyNoMoreInteractions(view, interactor)
    }
}