package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.view.PlayersView

interface PlayerPresenter : BasicPresenter {

    fun init(view: PlayersView)

    fun loadPlayers()

    fun addPlayer()

    fun editPlayer(player: Player)

    fun editPlayers(players: List<Player>)

    fun removePlayer(player: Player)

}