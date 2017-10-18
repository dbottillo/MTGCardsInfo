package com.dbottillo.mtgsearchfree.ui.decks

import android.net.Uri
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit

class DecksFragmentPresenterImplTest {

    lateinit var underTest: DecksFragmentPresenter

    @Mock
    lateinit var interactor: DecksInteractor
    @Mock
    lateinit var logger: Logger
    @Mock
    lateinit var view: DecksFragmentView
    @Mock
    lateinit var decks: List<Deck>
    @Mock
    lateinit var deck: Deck
    @Mock
    lateinit var uri: Uri
    @Mock
    lateinit var exception: MTGException
    @Mock
    lateinit var genericException: Exception

    @Rule @JvmField
    val mockitoRule = MockitoJUnit.rule()

    @Before
    fun setUp() {
        underTest = DecksFragmentPresenterImpl(interactor, logger)
        underTest.init(view)
    }

    @Test
    fun `load decks, should call interactor and update view`() {
        `when`(interactor.load()).thenReturn(Single.just(decks))

        underTest.loadDecks()

        verify(view).decksLoaded(decks)
        verify(interactor).load()
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `load decks, should call interactor and show error if there is an exception`() {
        `when`(exception.message).thenReturn("error")
        `when`(interactor.load()).thenReturn(Single.error(exception))

        underTest.loadDecks()

        verify(view).showError("error")
        verify(interactor).load()
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `add deck, should call interactor and update view`() {
        `when`(interactor.addDeck("new deck")).thenReturn(Observable.just(decks))

        underTest.addDeck("new deck")

        verify(view).decksLoaded(decks)
        verify(interactor).addDeck("new deck")
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `add deck, should call interactor and show error if there is an exception`() {
        `when`(genericException.localizedMessage).thenReturn("error")
        `when`(interactor.addDeck("new deck")).thenReturn(Observable.error(genericException))

        underTest.addDeck("new deck")

        verify(view).showError("error")
        verify(interactor).addDeck("new deck")
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `delete deck, should call interactor and update view`() {
        `when`(exception.message).thenReturn("error")
        `when`(interactor.deleteDeck(deck)).thenReturn(Observable.error(exception))

        underTest.deleteDeck(deck)

        verify(view).showError("error")
        verify(interactor).deleteDeck(deck)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `delete deck, should call interactor and show error if there is an exception`() {
        `when`(interactor.deleteDeck(deck)).thenReturn(Observable.just(decks))

        underTest.deleteDeck(deck)

        verify(view).decksLoaded(decks)
        verify(interactor).deleteDeck(deck)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `import deck, should call interactor and update view`() {
        `when`(interactor.importDeck(uri)).thenReturn(Observable.just(decks))

        underTest.importDeck(uri)

        verify(view).decksLoaded(decks)
        verify(interactor).importDeck(uri)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `umport deck, should call interactor and show error if there is an exception`() {
        `when`(genericException.localizedMessage).thenReturn("error")
        `when`(interactor.importDeck(uri)).thenReturn(Observable.error(genericException))

        underTest.importDeck(uri)

        verify(view).showError("error")
        verify(interactor).importDeck(uri)
        verifyNoMoreInteractions(view, interactor)
    }


}