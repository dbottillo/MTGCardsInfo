package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor
import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.view.PlayersView
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class PlayerPresenterImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: PlayerPresenter

    @Mock
    lateinit var interactor: PlayerInteractor

    @Mock
    lateinit var view: PlayersView

    @Mock
    lateinit var player: Player

    lateinit var players: MutableList<Player>

    @Mock
    lateinit var toEdit: List<Player>

    @Mock
    internal lateinit var logger: Logger

    @Before
    fun setup() {
        players = ArrayList<Player>()
        players.add(Player(1, "Liliana"))
        players.add(Player(2, "Jayce"))

        `when`(interactor.load()).thenReturn(Observable.just<List<Player>>(players))
        `when`(interactor.addPlayer()).thenReturn(Observable.just<List<Player>>(players))
        `when`(interactor.editPlayer(player)).thenReturn(Observable.just<List<Player>>(players))
        `when`(interactor.removePlayer(player)).thenReturn(Observable.just<List<Player>>(players))
        `when`(interactor.editPlayers(toEdit)).thenReturn(Observable.just<List<Player>>(players))

        underTest = PlayerPresenterImpl(interactor, TestRunnerFactory(), logger)
        underTest.init(view)
    }

    @Test
    fun testLoadPlayers() {
        underTest.loadPlayers()
        verify<PlayerInteractor>(interactor).load()
        verify<PlayersView>(view).showLoading()
        verify<PlayersView>(view).playersLoaded(players)
    }

    @Test
    fun testAddPlayer() {
        underTest.loadPlayers()
        underTest.addPlayer()
        verify<PlayerInteractor>(interactor).addPlayer()
        verify<PlayersView>(view, times(2)).showLoading()
        verify<PlayersView>(view, times(2)).playersLoaded(players)
    }

    @Test
    fun testEditPlayer() {
        underTest.editPlayer(player)
        verify<PlayerInteractor>(interactor).editPlayer(player)
        verify<PlayersView>(view).showLoading()
        verify<PlayersView>(view).playersLoaded(players)
    }

    @Test
    fun testEditPlayers() {
        underTest.editPlayers(toEdit)
        verify<PlayerInteractor>(interactor).editPlayers(toEdit)
        verify<PlayersView>(view).showLoading()
        verify<PlayersView>(view).playersLoaded(players)
    }

    @Test
    fun testRemovePlayer() {
        underTest.removePlayer(player)
        verify<PlayerInteractor>(interactor).removePlayer(player)
        verify<PlayersView>(view).showLoading()
        verify<PlayersView>(view).playersLoaded(players)
    }
}