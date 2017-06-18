package com.dbottillo.mtgsearchfree.ui.saved.lifecounter

import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor
import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.presenter.TestRunnerFactory
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterPresenter
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterPresenterImpl
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterView
import io.reactivex.Observable
import net.bytebuddy.implementation.bytecode.Throw
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class LifeCounterPresenterImplTest {

    @Rule @JvmField
    var mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: LifeCounterPresenterImpl

    @Mock
    lateinit var interactor: PlayerInteractor

    @Mock
    lateinit var view: LifeCounterView

    @Mock
    lateinit var player: Player

    @Mock
    lateinit var players: List<Player>

    @Mock
    lateinit var toEdit: List<Player>

    @Mock
    lateinit var error: Throwable

    @Mock
    internal lateinit var logger: Logger

    @Before
    fun setup() {
        underTest = LifeCounterPresenterImpl(interactor, logger)
        underTest.init(view)
    }

    @Test
    fun `load players should call interactor and show loading`() {
        `when`(interactor.load()).thenReturn(Observable.just<List<Player>>(players))

        underTest.loadPlayers()

        verify(interactor).load()
        verify(view).playersLoaded(players)
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `add player should call interactor and show loading`() {
        `when`(interactor.addPlayer()).thenReturn(Observable.just<List<Player>>(players))

        underTest.addPlayer()

        verify(interactor).addPlayer()
        verify(view).playersLoaded(players)
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `edit player should call interactor and show loading`() {
        `when`(interactor.editPlayer(player)).thenReturn(Observable.just<List<Player>>(players))

        underTest.editPlayer(player)

        verify(interactor).editPlayer(player)
        verify(view).playersLoaded(players)
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `edit players should call interactor and show loading`() {
        `when`(interactor.editPlayers(toEdit)).thenReturn(Observable.just<List<Player>>(players))

        underTest.editPlayers(toEdit)

        verify(interactor).editPlayers(toEdit)
        verify(view).playersLoaded(players)
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `remove player should call interactor and show loading`() {
        `when`(interactor.removePlayer(player)).thenReturn(Observable.just<List<Player>>(players))

        underTest.removePlayer(player)

        verify(interactor).removePlayer(player)
        verify(view).playersLoaded(players)
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `players loaded should update the view and remove loading`() {
        underTest.playersLoaded(players)

        verify(view).playersLoaded(players)
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `show error should show the error in the view and remove loading`() {
        Mockito.`when`(error.localizedMessage).thenReturn("error")

        underTest.showError(error)

        verify(view).showError("error")
        verifyNoMoreInteractions(interactor, view)
    }
}