package com.dbottillo.mtgsearchfree.ui.lifecounter

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.ui.BasicView

interface LifeCounterView : BasicView {
    fun playersLoaded(newPlayers: List<Player>)
}