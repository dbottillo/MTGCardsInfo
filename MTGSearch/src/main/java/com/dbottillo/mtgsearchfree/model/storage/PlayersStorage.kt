package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.model.database.PlayerDataSource
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.util.StringUtil
import java.util.*

interface PlayersStorage{
    fun load(): List<Player>
    fun addPlayer(): List<Player>
    fun editPlayer(player: Player): List<Player>
    fun editPlayers(players: List<Player>): List<Player>
    fun removePlayer(player: Player): List<Player>
}
