package com.dbottillo.mtgsearchfree.lifecounter

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.util.Logger
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class LifeCounterPresenterImplTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: LifeCounterPresenterImpl

    @Mock lateinit var interactor: PlayerInteractor
    @Mock lateinit var view: LifeCounterView
    @Mock lateinit var player: Player
    @Mock lateinit var players: List<Player>
    @Mock lateinit var toEdit: List<Player>
    @Mock lateinit var error: Throwable
    @Mock internal lateinit var logger: Logger

    @Before
    fun setup() {
        underTest = LifeCounterPresenterImpl(interactor, logger)
        underTest.init(view)
    }

    @Test
    fun `load players should call interactor and show loading`() {
        whenever(interactor.load()).thenReturn(Observable.just(players))

        underTest.loadPlayers()

        verify(interactor).load()
        verify(view).playersLoaded(players)
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `add player should call interactor and show loading`() {
        whenever(interactor.addPlayer()).thenReturn(Observable.just(players))

        underTest.addPlayer()

        verify(interactor).addPlayer()
        verify(view).playersLoaded(players)
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `edit player should call interactor and show loading`() {
        whenever(interactor.editPlayer(player)).thenReturn(Observable.just(players))

        underTest.editPlayer(player)

        verify(interactor).editPlayer(player)
        verify(view).playersLoaded(players)
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `edit players should call interactor and show loading`() {
        whenever(interactor.editPlayers(toEdit)).thenReturn(Observable.just(players))

        underTest.editPlayers(toEdit)

        verify(interactor).editPlayers(toEdit)
        verify(view).playersLoaded(players)
        verifyNoMoreInteractions(interactor, view)
    }

    @Test
    fun `remove player should call interactor and show loading`() {
        whenever(interactor.removePlayer(player)).thenReturn(Observable.just(players))

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
        whenever(error.localizedMessage).thenReturn("error")

        underTest.showError(error)

        verify(view).showError("error")
        verifyNoMoreInteractions(interactor, view)
    }
}