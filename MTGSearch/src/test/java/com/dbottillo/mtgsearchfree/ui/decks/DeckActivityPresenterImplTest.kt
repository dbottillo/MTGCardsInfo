package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class DeckActivityPresenterImplTest {

    @Rule
    @JvmField
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var interactor: DecksInteractor
    @Mock
    internal lateinit var logger: Logger
    @Mock
    lateinit var view: DeckActivityView
    @Mock
    lateinit var deck: Deck
    @Mock
    lateinit var card: MTGCard
    @Mock
    lateinit var cards: DeckCollection
    @Mock
    lateinit var cardsToExport: CardsCollection
    @Mock
    lateinit var decks: List<Deck>

    lateinit var underTest: DeckActivityPresenter

    @Before
    fun setup() {
        underTest = DeckActivityPresenterImpl(interactor, logger)
        underTest.init(view)
    }

    @Test
    fun `load deck, should call interactor and update view`() {
        `when`(interactor.loadDeck(deck)).thenReturn(Observable.just(cards))

        underTest.loadDeck(deck)

        verify(view).deckLoaded(cards)
        verify(interactor).loadDeck(deck)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `add card to deck, should call interactor and update view`() {
        `when`(interactor.addCard(deck, card, 6)).thenReturn(Observable.just(cards))

        underTest.addCardToDeck(deck, card, 6)

        verify(view).deckLoaded(cards)
        verify(interactor).addCard(deck, card, 6)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `remove card from deck, should call interactor and update view`() {
        `when`(interactor.removeCard(deck, card)).thenReturn(Observable.just(cards))

        underTest.removeCardFromDeck(deck, card)

        verify(view).deckLoaded(cards)
        verify(interactor).removeCard(deck, card)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `remove all cards from deck, should call interactor and update view`() {
        `when`(interactor.removeAllCard(deck, card)).thenReturn(Observable.just(cards))

        underTest.removeAllCardFromDeck(deck, card)

        verify(view).deckLoaded(cards)
        verify(interactor).removeAllCard(deck, card)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `move card from sideboard, should call interactor and update view`() {
        `when`(interactor.moveCardFromSideboard(deck, card, 6)).thenReturn(Observable.just(cards))

        underTest.moveCardFromSideBoard(deck, card, 6)

        verify(view).deckLoaded(cards)
        verify(interactor).moveCardFromSideboard(deck, card, 6)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `move card to sideboard, should call interactor and update view`() {
        `when`(interactor.moveCardToSideboard(deck, card, 6)).thenReturn(Observable.just(cards))

        underTest.moveCardToSideBoard(deck, card, 6)

        verify(view).deckLoaded(cards)
        verify(interactor).moveCardToSideboard(deck, card, 6)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `edit deck name, should call interactor and update view`() {
        `when`(interactor.editDeck(deck, "new name")).thenReturn(Observable.just(cards))

        underTest.editDeck(deck, "new name")

        verify(view).deckLoaded(cards)
        verify(interactor).editDeck(deck, "new name")
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `export deck, should call interactor and update view`() {
        `when`(interactor.exportDeck(deck, cardsToExport)).thenReturn(Observable.just(true))

        underTest.exportDeck(deck, cardsToExport)

        verify(view).deckExported(true)
        verify(interactor).exportDeck(deck, cardsToExport)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `copy deck, should call interactor and update view`() {
        `when`(interactor.copy(deck)).thenReturn(Single.just(decks))

        underTest.copyDeck(deck)

        verify(view).deckCopied()
        verify(interactor).copy(deck)
        verifyNoMoreInteractions(view, interactor)
    }
}