package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.storage.CardsPreferencesImpl
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import io.reactivex.observers.TestObserver

class CardFilterInteractorImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var cardFilter: CardFilter
    @Mock lateinit var cardsPreferences: CardsPreferencesImpl
    @Mock lateinit var logger: Logger

    lateinit var underTest: CardFilterInteractor

    @Before
    fun setUp() {
        underTest = CardFilterInteractorImpl(cardsPreferences, logger)
    }

    @Test
    fun willLoadDataFromStorage() {
        whenever(cardsPreferences.load()).thenReturn(cardFilter)
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