package com.dbottillo.mtgsearchfree.ui.cardsCoonfigurator

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.model.CardFilter
import io.reactivex.Observable
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnit

class CardsConfiguratorPresenterImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var cardFilterInteractor: CardFilterInteractor
    @Mock
    lateinit var view: CardsConfiguratorView
    @Mock
    lateinit var filter: CardFilter

    lateinit var underTest: CardsConfiguratorPresenter

    @Before
    fun setUp() {
        Mockito.`when`(cardFilterInteractor.load()).thenReturn(Observable.just(filter))
        underTest = CardsConfiguratorPresenterImpl(cardFilterInteractor)
    }

    @Test
    fun `init should call interactor and update view`() {
        underTest.init(view)

        verify(cardFilterInteractor).load()
        verify(view).loadFilter(filter)
        verifyNoMoreInteractions(view, cardFilterInteractor)
    }

    @Test
    fun `update filter, should call interactor and view with updated filter`() {
        underTest.init(view)
        Mockito.reset(view, cardFilterInteractor)

        underTest.update(CardFilter.TYPE.BLUE, true)

        val captor = ArgumentCaptor.forClass(CardFilter::class.java)
        verify(cardFilterInteractor).sync(captor.capture())
        assertTrue(captor.value.blue)
        verify(view).loadFilter(captor.value)
        verifyNoMoreInteractions(view, cardFilterInteractor)
    }
}
