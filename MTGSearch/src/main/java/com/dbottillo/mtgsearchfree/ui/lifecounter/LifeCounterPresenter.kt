package com.dbottillo.mtgsearchfree.ui.lifecounter

interface LifeCounterPresenter {

    fun init(view: LifeCounterView)

    fun loadPlayers()

    fun addPlayer()

    fun editPlayer(player: com.dbottillo.mtgsearchfree.model.Player)

    fun editPlayers(players: List<com.dbottillo.mtgsearchfree.model.Player>)

    fun removePlayer(player: com.dbottillo.mtgsearchfree.model.Player)

}