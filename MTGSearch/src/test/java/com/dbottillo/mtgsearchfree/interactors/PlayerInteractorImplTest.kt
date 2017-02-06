package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage
import com.dbottillo.mtgsearchfree.util.Logger

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import java.util.Arrays

import io.reactivex.observers.TestObserver

import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class PlayerInteractorImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    internal lateinit var storage: PlayersStorage
    @Mock
    internal lateinit var player: Player
    @Mock
    internal lateinit var logger: Logger

    private val players = Arrays.asList(Player(1, "Jace"), Player(2, "Liliana"))
    private val toEditPlayers = Arrays.asList(Player(3, "Chandra"), Player(4, "Sorin"))

    private lateinit var underTest: PlayerInteractor

    @Before
    fun setup() {
        `when`(storage.load()).thenReturn(players)
        `when`(storage.addPlayer()).thenReturn(players)
        `when`(storage.removePlayer(player)).thenReturn(players)
        `when`(storage.editPlayer(player)).thenReturn(players)
        `when`(storage.editPlayers(toEditPlayers)).thenReturn(players)
        underTest = PlayerInteractorImpl(storage, logger)
    }

    @Test
    fun testLoad() {
        val testSubscriber = TestObserver<List<Player>>()
        underTest.load().subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify<PlayersStorage>(storage).load()
    }

    @Test
    fun testAddPlayer() {
        val testSubscriber = TestObserver<List<Player>>()
        underTest.addPlayer().subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify<PlayersStorage>(storage).addPlayer()
    }

    @Test
    fun testEditPlayer() {
        val testSubscriber = TestObserver<List<Player>>()
        underTest.editPlayer(player).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify<PlayersStorage>(storage).editPlayer(player)
    }

    @Test
    fun testEditPlayers() {
        val testSubscriber = TestObserver<List<Player>>()
        underTest.editPlayers(toEditPlayers).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify<PlayersStorage>(storage).editPlayers(toEditPlayers)
    }

    @Test
    fun testRemovePlayer() {
        val testSubscriber = TestObserver<List<Player>>()
        underTest.removePlayer(player).subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(players)
        verify<PlayersStorage>(storage).removePlayer(player)
    }
}