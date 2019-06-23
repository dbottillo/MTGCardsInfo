package com.dbottillo.mtgsearchfree.lifecounter

import com.dbottillo.mtgsearchfree.model.Player

interface PlayersStorage {
    fun load(): List<Player>
    fun addPlayer(): List<Player>
    fun editPlayer(player: Player): List<Player>
    fun editPlayers(players: List<Player>): List<Player>
    fun removePlayer(player: Player): List<Player>
}
