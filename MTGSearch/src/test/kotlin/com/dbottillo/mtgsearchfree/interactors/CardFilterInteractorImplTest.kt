package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferencesImpl
import com.dbottillo.mtgsearchfree.util.Logger

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import io.reactivex.observers.TestObserver
import org.mockito.Mockito.*

class CardFilterInteractorImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var cardFilter: CardFilter

    @Mock
    lateinit var cardsPreferences: CardsPreferencesImpl

    @Mock
    lateinit var logger: Logger

    lateinit var underTest: CardFilterInteractor

    @Before
    fun setUp() {
        underTest = CardFilterInteractorImpl(cardsPreferences, logger)
    }

    @Test
    fun willLoadDataFromStorage() {
        `when`(cardsPreferences.load()).thenReturn(cardFilter)
        val testSubscriber = TestObserver<CardFilter>()

        underTest.load().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(cardFilter)
        verify(cardsPreferences).load()
        verifyNoMoreInteractions(cardsPreferences)
    }

    @Test
    fun willSyncDataWithStorage() {
        underTest.sync(cardFilter)

        verify(cardsPreferences).sync(cardFilter)
        verifyNoMoreInteractions(cardsPreferences)
    }

}