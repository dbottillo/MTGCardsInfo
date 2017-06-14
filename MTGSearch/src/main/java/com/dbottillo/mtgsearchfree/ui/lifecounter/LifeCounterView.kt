package com.dbottillo.mtgsearchfree.ui.lifecounter

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.view.BasicView

interface LifeCounterView : BasicView {

    fun playersLoaded(players: List<Player>)

    fun showLoading()
}