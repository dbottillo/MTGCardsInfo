package com.dbottillo.mtgsearchfree.lifecounter

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LifeCounterPresenterImpl @Inject
constructor(
    private val interactor: PlayerInteractor,
    private val logger: Logger
) : LifeCounterPresenter {

    private lateinit var playerView: LifeCounterView

    init {
        logger.d("created")
    }

    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun init(view: LifeCounterView) {
        logger.d()
        playerView = view
    }

    override fun loadPlayers() {
        logger.d()
        disposable.add(interactor.load().subscribe({
            playersLoaded(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    override fun addPlayer() {
        logger.d()
        disposable.add(interactor.addPlayer().subscribe({
            playersLoaded(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    override fun editPlayer(player: Player) {
        logger.d()
        disposable.add(interactor.editPlayer(player).subscribe({
            playersLoaded(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    override fun editPlayers(players: List<Player>) {
        logger.d()
        disposable.add(interactor.editPlayers(players).subscribe({
            playersLoaded(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    override fun removePlayer(player: Player) {
        logger.d()
        disposable.add(interactor.removePlayer(player).subscribe({
            playersLoaded(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    fun playersLoaded(newPlayers: List<Player>) {
        logger.d()
        playerView.playersLoaded(newPlayers)
    }

    fun showError(throwable: Throwable) {
        logger.e(throwable)
        playerView.showError(throwable.localizedMessage ?: "")
    }

    override fun onDestroy() {
        disposable.clear()
    }
}
