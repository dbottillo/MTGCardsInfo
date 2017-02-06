package com.dbottillo.mtgsearchfree.ui.lifecounter

import com.dbottillo.mtgsearchfree.model.Player

interface OnLifeCounterListener{
    fun onRemovePlayer(player: Player)

    fun onEditPlayer(player: Player)

    fun onLifeCountChange(player: Player, value: Int)

    fun onPoisonCountChange(player: Player, value: Int)
}
