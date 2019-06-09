package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.interactor.SchedulerProvider
import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.storage.PlayersStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable

class PlayerInteractorImpl(
    private val storage: PlayersStorage,
    private val schedulerProvider: SchedulerProvider,
    private val logger: Logger
) : PlayerInteractor {

    init {
        logger.d("created")
    }

    override fun load(): Observable<List<Player>> {
        logger.d("loadSet")
        return Observable.fromCallable { storage.load() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun addPlayer(): Observable<List<Player>> {
        logger.d("add player")
        return Observable.fromCallable { storage.addPlayer() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun editPlayer(player: Player): Observable<List<Player>> {
        logger.d("edit " + player)
        return Observable.fromCallable { storage.editPlayer(player) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun editPlayers(players: List<Player>): Observable<List<Player>> {
        logger.d("update players " + players.toString())
        return Observable.fromCallable { storage.editPlayers(players) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun removePlayer(player: Player): Observable<List<Player>> {
        logger.d("remove " + player)
        return Observable.fromCallable { storage.removePlayer(player) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }
}
