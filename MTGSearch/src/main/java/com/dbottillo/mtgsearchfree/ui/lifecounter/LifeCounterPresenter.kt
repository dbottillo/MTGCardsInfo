package com.dbottillo.mtgsearchfree.ui.lifecounter

import com.dbottillo.mtgsearchfree.model.Player

interface LifeCounterPresenter {
    fun init(view: LifeCounterView)
    fun loadPlayers()
    fun addPlayer()
    fun editPlayer(player: Player)
    fun editPlayers(players: List<Player>)
    fun removePlayer(player: Player)
}