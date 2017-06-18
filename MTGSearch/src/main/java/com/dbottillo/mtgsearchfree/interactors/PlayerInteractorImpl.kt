package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PlayerInteractorImpl(private val storage: PlayersStorage, private val logger: Logger) : PlayerInteractor {

    init {
        logger.d("created")
    }

    override fun load(): Observable<List<Player>> {
        logger.d("loadSet")
        return Observable.just(storage.load())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun addPlayer(): Observable<List<Player>> {
        logger.d("add player")
        return Observable.just(storage.addPlayer())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun editPlayer(player: Player): Observable<List<Player>> {
        logger.d("edit " + player)
        return Observable.just(storage.editPlayer(player))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun editPlayers(players: List<Player>): Observable<List<Player>> {
        logger.d("update players " + players.toString())
        return Observable.just(storage.editPlayers(players))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun removePlayer(player: Player): Observable<List<Player>> {
        logger.d("remove " + player)
        return Observable.just(storage.removePlayer(player))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}
