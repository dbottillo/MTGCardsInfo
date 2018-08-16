package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.model.database.PlayerDataSource
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.whenever

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import java.util.Arrays

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

class PlayersStorageImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    @Mock lateinit var playerDataSource: PlayerDataSource
    @Mock lateinit var player: Player
    @Mock lateinit var logger: Logger

    private val players = Arrays.asList(Player(1, "Jayce"), Player(2, "Liliana"))

    lateinit var underTest: PlayersStorage

    @Before
    fun setup() {
        whenever(playerDataSource.players).thenReturn(players)
        underTest = PlayersStorageImpl(playerDataSource, logger)
    }

    @Test
    fun testLoad() {
        val result = underTest.load()

        verify(playerDataSource).players

        assertThat(result, `is`(players))
    }

    @Test
    fun shouldReturnAtLeastOnePlayer_whenStorageReturnsEmpty() {
        whenever(playerDataSource.players).thenReturn(listOf())

        underTest.load()

        verify(playerDataSource).savePlayer(argumentCaptor<Player>().capture())
        verify(playerDataSource, times(3)).players
    }

    @Test
    fun testAddPlayer() {
        val result = underTest.addPlayer()

        verify(playerDataSource).savePlayer(argumentCaptor<Player>().capture())
        verify(playerDataSource, times(2)).players

        assertThat(result, `is`(players))
    }

    @Test
    fun testEditPlayer() {
        val result = underTest.editPlayer(player)
        verify(playerDataSource).savePlayer(player)
        assertThat(result, `is`(players))
    }

    @Test
    fun testEditPlayers() {
        val player1 = mock(Player::class.java)
        val player2 = mock(Player::class.java)
        val toEdit = Arrays.asList(player1, player2)
        val result = underTest.editPlayers(toEdit)
        verify(playerDataSource).savePlayer(player1)
        verify(playerDataSource).savePlayer(player2)
        assertThat(result, `is`(players))
    }

    @Test
    fun testRemovePlayer() {
        val result = underTest.removePlayer(player)
        verify(playerDataSource).removePlayer(player)
        assertThat(result, `is`(players))
    }
}