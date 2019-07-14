package com.dbottillo.mtgsearchfree.ui.cardsConfigurator

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnit

class CardsConfiguratorPresenterImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var cardFilterInteractor: CardFilterInteractor
    @Mock lateinit var view: com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorView
    @Mock lateinit var logger: Logger

    private var filter = CardFilter()
    lateinit var underTest: com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorPresenter

    @Before
    fun setUp() {
        whenever(cardFilterInteractor.load()).thenReturn(Observable.just(filter))
        underTest = com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorPresenterImpl(cardFilterInteractor, logger)
    }

    @Test
    fun `init should call interactor and update view`() {
        underTest.init(view)

        verify(cardFilterInteractor).load()
        verify(view).loadFilter(filter = filter, refresh = false)
        verifyNoMoreInteractions(view, cardFilterInteractor)
    }

    @Test
    fun `update filter, should call interactor and view with updated filter`() {
        underTest.init(view)
        Mockito.reset(view, cardFilterInteractor)

        underTest.update(CardFilter.TYPE.BLUE, true)

        argumentCaptor<CardFilter>().apply {
            verify(cardFilterInteractor).sync(capture())
            assertTrue(firstValue.blue)
            verify(view).loadFilter(filter = firstValue, refresh = true)
        }

        verifyNoMoreInteractions(view, cardFilterInteractor)
    }
}
