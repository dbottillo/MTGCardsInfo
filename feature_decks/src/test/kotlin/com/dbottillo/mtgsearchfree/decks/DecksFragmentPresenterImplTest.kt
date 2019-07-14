package com.dbottillo.mtgsearchfree.decks

import android.net.Uri
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class DecksFragmentPresenterImplTest {

    @Rule @JvmField val mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: DecksFragmentPresenter

    @Mock lateinit var interactor: DecksInteractor
    @Mock lateinit var logger: Logger
    @Mock lateinit var view: DecksFragmentView
    @Mock lateinit var decks: List<Deck>
    @Mock lateinit var deck: Deck
    @Mock lateinit var uri: Uri
    @Mock lateinit var exception: MTGException
    @Mock lateinit var genericException: Exception

    @Before
    fun setUp() {
        underTest = DecksFragmentPresenterImpl(interactor, logger)
        underTest.init(view)
    }

    @Test
    fun `load decks, should call interactor and update view`() {
        whenever(interactor.load()).thenReturn(Single.just(decks))

        underTest.loadDecks()

        verify(view).decksLoaded(decks)
        verify(interactor).load()
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `load decks, should call interactor and show error if there is an exception`() {
        whenever(exception.message).thenReturn("error")
        whenever(interactor.load()).thenReturn(Single.error(exception))

        underTest.loadDecks()

        verify(view).showError("error")
        verify(interactor).load()
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `add deck, should call interactor and update view`() {
        whenever(interactor.addDeck("new deck")).thenReturn(Observable.just(decks))

        underTest.addDeck("new deck")

        verify(view).decksLoaded(decks)
        verify(interactor).addDeck("new deck")
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `add deck, should call interactor and show error if there is an exception`() {
        whenever(genericException.localizedMessage).thenReturn("error")
        whenever(interactor.addDeck("new deck")).thenReturn(Observable.error(genericException))

        underTest.addDeck("new deck")

        verify(view).showError("error")
        verify(interactor).addDeck("new deck")
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `delete deck, should call interactor and update view`() {
        whenever(exception.message).thenReturn("error")
        whenever(interactor.deleteDeck(deck)).thenReturn(Observable.error(exception))

        underTest.deleteDeck(deck)

        verify(view).showError("error")
        verify(interactor).deleteDeck(deck)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `delete deck, should call interactor and show error if there is an exception`() {
        whenever(interactor.deleteDeck(deck)).thenReturn(Observable.just(decks))

        underTest.deleteDeck(deck)

        verify(view).decksLoaded(decks)
        verify(interactor).deleteDeck(deck)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `import deck, should call interactor and update view`() {
        whenever(interactor.importDeck(uri)).thenReturn(Observable.just(decks))

        underTest.importDeck(uri)

        verify(view).decksLoaded(decks)
        verify(interactor).importDeck(uri)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `import deck, should call interactor and show error if there is an exception`() {
        whenever(genericException.localizedMessage).thenReturn("error")
        whenever(interactor.importDeck(uri)).thenReturn(Observable.error(genericException))

        underTest.importDeck(uri)

        verify(view).showError("error")
        verify(interactor).importDeck(uri)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `copy deck, should call interactor and update view`() {
        whenever(interactor.copy(deck)).thenReturn(Single.just(decks))

        underTest.copyDeck(deck)

        verify(view).decksLoaded(decks)
        verify(interactor).copy(deck)
        verifyNoMoreInteractions(view, interactor)
    }

    @Test
    fun `copy deck, should call interactor and show error if there is an exception`() {
        whenever(genericException.localizedMessage).thenReturn("error")
        whenever(interactor.copy(deck)).thenReturn(Single.error(genericException))

        underTest.copyDeck(deck)

        verify(view).showError("error")
        verify(interactor).copy(deck)
        verifyNoMoreInteractions(view, interactor)
    }
}