package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor
import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.util.StringUtil
import com.dbottillo.mtgsearchfree.view.PlayersView
import java.util.Random

import javax.inject.Inject

class PlayerPresenterImpl @Inject
constructor(private val interactor: PlayerInteractor, runnerFactory: RunnerFactory, private val logger: Logger) : PlayerPresenter, Runner.RxWrapperListener<List<Player>> {
    
    private val runner: Runner<List<Player>> = runnerFactory.simple<List<Player>>()

    private lateinit var playerView: PlayersView
    private var players: List<Player>? = null


    init {
        logger.d("created")
    }

    override fun init(view: PlayersView) {
        logger.d()
        playerView = view
    }

    override fun loadPlayers() {
        logger.d()
        playerView.showLoading()
        runner.run(interactor.load(), this)
    }

    override fun addPlayer() {
        logger.d()
        /*val player = Player(uniqueIdForPlayer, uniqueNameForPlayer)*/
        playerView.showLoading()
        runner.run(interactor.addPlayer(), this)
    }

    override fun editPlayer(player: Player) {
        logger.d()
        playerView.showLoading()
        runner.run(interactor.editPlayer(player), this)
    }

    override fun editPlayers(players: List<Player>) {
        logger.d()
        playerView.showLoading()
        runner.run(interactor.editPlayers(players), this)
    }

    override fun removePlayer(player: Player) {
        logger.d()
        playerView.showLoading()
        runner.run(interactor.removePlayer(player), this)
    }

    override fun onNext(newPlayers: List<Player>) {
        logger.d()
        players = newPlayers
        playerView.playersLoaded(players!!)
    }

    override fun onError(e: Throwable) {
        LOG.e(e.localizedMessage)
    }

    override fun onCompleted() {
        logger.d()
    }




    override fun detachView() {

    }
}