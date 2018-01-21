package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.storage.ReleaseNoteStorage
import com.dbottillo.mtgsearchfree.ui.about.ReleaseNoteItem
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class ReleaseNoteInteractorTest {

    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock lateinit var schedulerProvider: SchedulerProvider
    @Mock lateinit var storage: ReleaseNoteStorage
    @Mock lateinit var logger: Logger
    @Mock lateinit var releaseNote: List<ReleaseNoteItem>
    @Mock lateinit var deck: Deck

    private lateinit var underTest: ReleaseNoteInteractor

    @Before
    fun setup() {
        whenever(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        whenever(schedulerProvider.ui()).thenReturn(Schedulers.trampoline())
        underTest = ReleaseNoteInteractor(storage, schedulerProvider, logger)
    }

    @Test
    fun `init should load release note`() {
        whenever(storage.load()).thenReturn(Single.just(releaseNote))
        val testObserver = TestObserver<List<ReleaseNoteItem>>()

        underTest.load().subscribe(testObserver)

        testObserver.assertComplete()
        testObserver.assertValue(releaseNote)
        verify(storage).load()
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

}