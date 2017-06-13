package com.dbottillo.mtgsearchfree.presenter

import android.net.Uri

import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.mapper.DeckMapper
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckBucket
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.view.DecksView

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import io.reactivex.Observable

import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class DecksPresenterImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: DecksPresenter

    @Mock
    lateinit var interactor: DecksInteractor

    @Mock
    lateinit var view: DecksView

    @Mock
    lateinit var deckMapper: DeckMapper

    @Mock
    lateinit var card: MTGCard

    @Mock
    lateinit var deck: Deck

    @Mock
    lateinit var uri: Uri

    @Mock
    lateinit var decks: List<Deck>

    @Mock
    lateinit var cards: CardsCollection

    @Mock
    lateinit var deckBucket: DeckBucket

    @Mock
    lateinit var logger: Logger

    @Before
    fun setup() {
        `when`(interactor.load()).thenReturn(Observable.just(decks))
        `when`(interactor.addDeck("deck")).thenReturn(Observable.just(decks))
        `when`(interactor.importDeck(uri)).thenReturn(Observable.just(decks))
        `when`(interactor.deleteDeck(deck)).thenReturn(Observable.just(decks))
        `when`(interactor.loadDeck(deck)).thenReturn(Observable.just(cards))
        `when`(interactor.addCard("new", card, 2)).thenReturn(Observable.just(cards))
        `when`(interactor.addCard(deck, card, 2)).thenReturn(Observable.just(cards))
        `when`(interactor.removeCard(deck, card)).thenReturn(Observable.just(cards))
        `when`(interactor.removeAllCard(deck, card)).thenReturn(Observable.just(cards))
        `when`(interactor.editDeck(deck, "deck")).thenReturn(Observable.just(cards))
        `when`(interactor.exportDeck(deck, cards)).thenReturn(Observable.just(true))
        `when`(interactor.moveCardFromSideboard(deck, card, 2)).thenReturn(Observable.just(cards))
        `when`(interactor.moveCardToSideboard(deck, card, 2)).thenReturn(Observable.just(cards))
        `when`(deckMapper.map(cards)).thenReturn(deckBucket)
        underTest = DecksPresenterImpl(interactor, deckMapper, TestRunnerFactory(), logger)
        underTest.init(view)
    }

    @Test
    fun testLoadDecks() {
        underTest.loadDecks()
        verify(interactor).load()
        verify(view).decksLoaded(decks)
    }

    @Test
    fun testLoadDeck() {
        underTest.loadDeck(deck)
        verify(interactor).loadDeck(deck)
        verify(view).deckLoaded(deckBucket)
    }

    @Test
    fun testAddDeck() {
        underTest.addDeck("deck")
        verify(interactor).addDeck("deck")
        verify(view).decksLoaded(decks)
    }

    @Test
    fun testDeleteDeck() {
        underTest.deleteDeck(deck)
        verify(interactor).deleteDeck(deck)
        verify(view).decksLoaded(decks)
    }

    @Test
    fun testEditDeck() {
        underTest.editDeck(deck, "deck")
        verify(interactor).editDeck(deck, "deck")
        verify(view).deckLoaded(deckBucket)
    }

    @Test
    fun testAddCardToDeck() {
        underTest.addCardToDeck(deck, card, 2)
        verify(interactor).addCard(deck, card, 2)
        verify(view).deckLoaded(deckBucket)
    }

    @Test
    fun testAddCardToDeckWithName() {
        underTest.addCardToDeck("new", card, 2)
        verify(interactor).addCard("new", card, 2)
        verify(view).deckLoaded(deckBucket)
    }

    @Test
    fun testRemoveCardFromDeck() {
        underTest.removeCardFromDeck(deck, card)
        verify(interactor).removeCard(deck, card)
        verify(view).deckLoaded(deckBucket)
    }

    @Test
    fun testRemoveAllCardFromDeck() {
        underTest.removeAllCardFromDeck(deck, card)
        verify(interactor).removeAllCard(deck, card)
        verify(view).deckLoaded(deckBucket)
    }

    @Test
    fun testImportDeck() {
        underTest.importDeck(uri)
        verify(interactor).importDeck(uri)
        verify(view).decksLoaded(decks)
    }

     @Test
    fun willExportDeck() {
        underTest.exportDeck(deck, cards)
        verify(interactor).exportDeck(deck, cards)
        verify(view).deckExported(true)
    }

    @Test
    fun willShowErrorIfDeckFileCannotBeImported() {
        val exception = MTGException(ExceptionCode.DECK_NOT_IMPORTED, "error")
        val observable = Observable.error<List<Deck>>(exception)
        `when`(interactor.importDeck(uri)).thenReturn(observable)
        underTest.importDeck(uri)
        verify(interactor).importDeck(uri)
        verify(view).showError(exception)
    }

    @Test
    fun movesCardFromSideboard() {
        underTest.moveCardFromSideBoard(deck, card, 2)
        verify(interactor).moveCardFromSideboard(deck, card, 2)
        verify(view).deckLoaded(deckBucket)
    }

    @Test
    fun movesCardToSideboard() {
        underTest.moveCardToSideBoard(deck, card, 2)
        verify(interactor).moveCardToSideboard(deck, card, 2)
        verify(view).deckLoaded(deckBucket)
    }
}