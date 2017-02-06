package com.dbottillo.mtgsearchfree.view

import com.dbottillo.mtgsearchfree.model.Player

interface PlayersView : BasicView {

    fun playersLoaded(players: List<Player>)

    fun showLoading()
}