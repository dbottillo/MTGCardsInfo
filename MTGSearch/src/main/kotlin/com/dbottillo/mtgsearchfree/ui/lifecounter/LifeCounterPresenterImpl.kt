package com.dbottillo.mtgsearchfree.ui.lifecounter

import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor
import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.util.Logger
import javax.inject.Inject

class LifeCounterPresenterImpl @Inject
constructor(val interactor: PlayerInteractor,
            val logger: Logger) : LifeCounterPresenter {

    private lateinit var playerView: LifeCounterView

    init {
        logger.d("created")
    }

    override fun init(view: LifeCounterView) {
        logger.d()
        playerView = view
    }

    override fun loadPlayers() {
        logger.d()
        interactor.load().subscribe({ playersLoaded(it) }, { showError(it) })
    }

    override fun addPlayer() {
        logger.d()
        interactor.addPlayer().subscribe({ playersLoaded(it) }, { showError(it) })
    }

    override fun editPlayer(player: Player) {
        logger.d()
        interactor.editPlayer(player).subscribe({ playersLoaded(it) }, { showError(it) })
    }

    override fun editPlayers(players: List<Player>) {
        logger.d()
        interactor.editPlayers(players).subscribe({ playersLoaded(it) }, { showError(it) })
    }

    override fun removePlayer(player: Player) {
        logger.d()
        interactor.removePlayer(player).subscribe({ playersLoaded(it) }, { showError(it) })
    }

    fun playersLoaded(newPlayers: List<Player>) {
        logger.d()
        playerView.playersLoaded(newPlayers)
    }

    fun showError(throwable: Throwable) {
        logger.e(throwable)
        playerView.showError(throwable.localizedMessage)
    }

}
