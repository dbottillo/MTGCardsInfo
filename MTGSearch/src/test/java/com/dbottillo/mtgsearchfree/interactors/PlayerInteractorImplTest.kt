package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.RxImmediateSchedulerRule
import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class PlayerInteractorImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Rule @JvmField
    var rxjavaRule = RxImmediateSchedulerRule()

    @Mock
    internal lateinit var storage: PlayersStorage
    @Mock
    internal lateinit var player: Player
    @Mock
    internal lateinit var logger: Logger
    @Mock
    lateinit var players: List<Player>
    
    private lateinit var underTest: PlayerInteractor

    @Before
    fun setup() {
        underTest = PlayerInteractorImpl(storage, logger)
    }

    @Test
    fun `load players, should call storage and return observable`() {
        `when`(storage.load()).thenReturn(players)
        val testSubscriber = TestObserver<List<Player>>()

        val result = underTest.load()

        result.subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify(storage).load()
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun `add player, should call storage and return observable`() {
        `when`(storage.addPlayer()).thenReturn(players)
        val testSubscriber = TestObserver<List<Player>>()

        underTest.addPlayer().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify(storage).addPlayer()
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun `edit player, should call storage and return observable`() {
        `when`(storage.editPlayer(player)).thenReturn(players)
        val testSubscriber = TestObserver<List<Player>>()
        
        underTest.editPlayer(player).subscribe(testSubscriber)
        
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify(storage).editPlayer(player)
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun `edit players, should call storage and return observable`() {
        `when`(storage.editPlayers(players)).thenReturn(players)
        val testSubscriber = TestObserver<List<Player>>()
        
        underTest.editPlayers(players).subscribe(testSubscriber)
        
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify(storage).editPlayers(players)
        verifyNoMoreInteractions(storage)
    }

    @Test
    fun `remove player, should call storage and return observable`() {
        `when`(storage.removePlayer(player)).thenReturn(players)
        val testSubscriber = TestObserver<List<Player>>()
        
        underTest.removePlayer(player).subscribe(testSubscriber)
        
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify(storage).removePlayer(player)
        verifyNoMoreInteractions(storage)
    }
}