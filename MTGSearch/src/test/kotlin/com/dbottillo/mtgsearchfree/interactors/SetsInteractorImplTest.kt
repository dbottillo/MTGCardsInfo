package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.database.SetDataSource
import com.dbottillo.mtgsearchfree.interactor.SchedulerProvider
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever

import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before

class SetsInteractorImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock private lateinit var logger: Logger
    @Mock private lateinit var dataSource: SetDataSource
    @Mock private lateinit var sets: List<MTGSet>
    @Mock lateinit var schedulerProvider: SchedulerProvider

    private lateinit var underTest: SetsInteractor

    @Before
    fun setUp() {
        whenever(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        whenever(schedulerProvider.ui()).thenReturn(Schedulers.trampoline())
        underTest = SetsInteractorImpl(dataSource, schedulerProvider, logger)
    }

    @Test
    fun `load should call interactor and return observable`() {
        whenever(dataSource.sets).thenReturn(sets)

        val result = underTest.load()

        val testSubscriber = TestObserver<List<MTGSet>>()
        result.subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(sets)
        verify(dataSource).sets
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(dataSource, schedulerProvider)
    }
}