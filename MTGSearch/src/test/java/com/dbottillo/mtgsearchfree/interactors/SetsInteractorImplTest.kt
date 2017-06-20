package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.RxImmediateSchedulerRule
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.database.SetDataSource
import com.dbottillo.mtgsearchfree.util.Logger

import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import java.util.Arrays

import io.reactivex.observers.TestObserver
import org.junit.Before
import org.mockito.Mockito.*

class SetsInteractorImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()!!

    @Rule @JvmField
    var rxjavaRule = RxImmediateSchedulerRule()

    @Mock
    private lateinit var logger: Logger

    @Mock
    private lateinit var dataSource: SetDataSource

    @Mock
    private lateinit var sets: List<MTGSet>

    private lateinit var underTest: SetsInteractor

    @Before
    fun setUp() {
        underTest = SetsInteractorImpl(dataSource, logger)
    }

    @Test
    fun `load should call interactor and return observable`() {
        `when`(dataSource.sets).thenReturn(sets)

        val result = underTest.load()

        val testSubscriber = TestObserver<List<MTGSet>>()
        result.subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(sets)
        verify(dataSource).sets
        verifyNoMoreInteractions(dataSource)
    }
}