package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.Player

import io.reactivex.Observable

interface PlayerInteractor {

    fun load(): Observable<List<Player>>

    fun addPlayer(): Observable<List<Player>>

    fun editPlayer(player: Player): Observable<List<Player>>

    fun editPlayers(players: List<Player>): Observable<List<Player>>

    fun removePlayer(player: Player): Observable<List<Player>>
}