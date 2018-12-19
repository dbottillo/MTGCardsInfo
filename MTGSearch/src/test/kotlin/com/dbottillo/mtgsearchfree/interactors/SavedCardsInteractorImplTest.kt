package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.storage.SavedCardsStorage
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnit

class SavedCardsInteractorImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var logger: Logger
    @Mock lateinit var storage: SavedCardsStorage
    @Mock lateinit var collection: CardsCollection
    @Mock lateinit var schedulerProvider: SchedulerProvider
    @Mock lateinit var card: MTGCard

    lateinit var underTest: SavedCardsInteractor
    var idFavs = intArrayOf()

    @Before
    fun setup() {
        whenever(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        whenever(schedulerProvider.ui()).thenReturn(Schedulers.trampoline())
        underTest = SavedCardsInteractorImpl(storage, schedulerProvider, logger)
    }

    @Test
    fun `load should call storage and returns observable`() {
        val testSubscriber = TestObserver<CardsCollection>()
        whenever(storage.load()).thenReturn(collection)

        underTest.load().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(collection)
        verify(storage).load()
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun `save card should call storage and returns observable`() {
        val testSubscriber = TestObserver<CardsCollection>()
        whenever(storage.load()).thenReturn(collection)

        underTest.save(card).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(collection)
        verify(storage).saveAsFavourite(card)
        verify(storage).load()
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun `remove card should call storage and returns observable`() {
        val testSubscriber = TestObserver<CardsCollection>()
        whenever(storage.load()).thenReturn(collection)

        underTest.remove(card).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(collection)
        verify(storage).removeFromFavourite(card)
        verify(storage).load()
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun `load id favs should call storage and returns observable`() {
        val testSubscriber = TestObserver<IntArray>()
        whenever(storage.loadIdFav()).thenReturn(idFavs)

        underTest.loadId().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(idFavs)
        verify(storage).loadIdFav()
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }
}