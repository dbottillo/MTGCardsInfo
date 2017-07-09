package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.RxImmediateSchedulerRule
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.storage.SavedCardsStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnit

class SavedCardsInteractorImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()!!

    @Rule @JvmField
    var rxjavaRule = RxImmediateSchedulerRule()

    @Mock
    lateinit var logger: Logger
    @Mock
    lateinit var storage: SavedCardsStorage
    @Mock
    lateinit var collection: CardsCollection

    lateinit var underTest: SavedCardsInteractor

    @Before
    fun setup() {
        underTest = SavedCardsInteractorImpl(storage, logger)
    }

    @Test
    fun `load should call storage and returns observable`() {
        val testSubscriber = TestObserver<CardsCollection>()
        `when`(storage.load()).thenReturn(collection)

        underTest.load().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(collection)
        verify(storage).load()
        verifyNoMoreInteractions(storage)
    }
}