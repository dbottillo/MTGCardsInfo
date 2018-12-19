package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.storage.PlayersStorage
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class PlayerInteractorImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock internal lateinit var storage: PlayersStorage
    @Mock internal lateinit var player: Player
    @Mock internal lateinit var logger: Logger
    @Mock lateinit var players: List<Player>
    @Mock lateinit var schedulerProvider: SchedulerProvider

    private lateinit var underTest: PlayerInteractor

    @Before
    fun setup() {
        whenever(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        whenever(schedulerProvider.ui()).thenReturn(Schedulers.trampoline())
        underTest = PlayerInteractorImpl(storage, schedulerProvider, logger)
    }

    @Test
    fun `load players, should call storage and return observable`() {
        whenever(storage.load()).thenReturn(players)
        val testSubscriber = TestObserver<List<Player>>()

        val result = underTest.load()

        result.subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify(storage).load()
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun `add player, should call storage and return observable`() {
        whenever(storage.addPlayer()).thenReturn(players)
        val testSubscriber = TestObserver<List<Player>>()

        underTest.addPlayer().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify(storage).addPlayer()
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun `edit player, should call storage and return observable`() {
        whenever(storage.editPlayer(player)).thenReturn(players)
        val testSubscriber = TestObserver<List<Player>>()

        underTest.editPlayer(player).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify(storage).editPlayer(player)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun `edit players, should call storage and return observable`() {
        whenever(storage.editPlayers(players)).thenReturn(players)
        val testSubscriber = TestObserver<List<Player>>()

        underTest.editPlayers(players).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify(storage).editPlayers(players)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }

    @Test
    fun `remove player, should call storage and return observable`() {
        whenever(storage.removePlayer(player)).thenReturn(players)
        val testSubscriber = TestObserver<List<Player>>()

        underTest.removePlayer(player).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify(storage).removePlayer(player)
        verify(schedulerProvider).io()
        verify(schedulerProvider).ui()
        verifyNoMoreInteractions(storage, schedulerProvider)
    }
}