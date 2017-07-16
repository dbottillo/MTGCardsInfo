package com.dbottillo.mtgsearchfree.ui.decks

import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class AddToDeckPresenterImplTest {

    @Rule @JvmField
    val mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: AddToDeckPresenter

    @Mock
    lateinit var interactor: DecksInteractor
    @Mock
    lateinit var logger: Logger
    @Mock
    lateinit var view: AddToDeckView
    @Mock
    lateinit var decks: List<Deck>
    @Mock
    lateinit var throwable: Throwable
    @Mock
    lateinit var deck: Deck
    @Mock
    lateinit var card: MTGCard
    @Mock
    lateinit var deckCollection: DeckCollection
    @Mock
    lateinit var mtgException: MTGException
    @Mock
    lateinit var observableDeckCollection: Observable<DeckCollection>

    @Before
    fun setUp() {
        underTest = AddToDeckPresenterImpl(interactor, logger)
        underTest.init(view)
    }

    @Test
    fun `load decks should call interactor and update view`() {
        Mockito.`when`(interactor.load()).thenReturn(Observable.just(decks))

        underTest.loadDecks()

        verify(interactor).load()
        verify(view).decksLoaded(decks)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `load decks should call interactor and show error it if fails`() {
        `when`(throwable.localizedMessage).thenReturn("error message")
        Mockito.`when`(interactor.load()).thenReturn(Observable.error(throwable))

        underTest.loadDecks()

        verify(interactor).load()
        verify(view).showError("error message")
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `load decks should call interactor and show error it if fails with a mtg exception`() {
        `when`(mtgException.message).thenReturn("error message")
        Mockito.`when`(interactor.load()).thenReturn(Observable.error(mtgException))

        underTest.loadDecks()

        verify(interactor).load()
        verify(view).showError("error message")
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `add card to existing deck should call interactor`() {
        Mockito.`when`(interactor.addCard(deck, card, 5)).thenReturn(observableDeckCollection)

        underTest.addCardToDeck(deck, card, 5)

        verify(interactor).addCard(deck, card, 5)
        verify(observableDeckCollection).subscribe()
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `add card to new deck should call interactor`() {
        Mockito.`when`(interactor.addCard("new deck", card, 5)).thenReturn(observableDeckCollection)

        underTest.addCardToDeck("new deck", card, 5)

        verify(interactor).addCard("new deck", card, 5)
        verify(observableDeckCollection).subscribe()
        verifyNoMoreInteractions(view, interactor)
    }

}