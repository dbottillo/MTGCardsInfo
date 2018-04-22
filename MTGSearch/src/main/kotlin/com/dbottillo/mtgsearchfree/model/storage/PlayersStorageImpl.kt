package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.model.database.PlayerDataSource
import com.dbottillo.mtgsearchfree.util.Logger
import java.util.*

open class PlayersStorageImpl(private val playerDataSource: PlayerDataSource,
                              private val logger: Logger) : PlayersStorage {

    private val names = arrayOf("Teferi", "Nicol Bolas", "Gerrard", "Ajani", "Jace", "Liliana", "Elspeth", "Tezzeret", "Garruck", "Chandra", "Venser", "Doran", "Sorin")

    init {
        logger.d("created")
    }

    override fun load(): List<Player> {
        logger.d()
        val listOfPlayer = playerDataSource.players
        if (listOfPlayer.isEmpty()) {
            return addPlayer()
        }
        return listOfPlayer
    }

    override fun addPlayer(): List<Player> {
        logger.d("add new player")
        playerDataSource.savePlayer(generatePlayer(playerDataSource.players))
        return playerDataSource.players
    }

    override fun editPlayer(player: Player): List<Player> {
        logger.d("edit $player")
        playerDataSource.savePlayer(player)
        return playerDataSource.players
    }

    override fun editPlayers(players: List<Player>): List<Player> {
        logger.d("update $players")
        for (player in players) {
            playerDataSource.savePlayer(player)
        }
        return playerDataSource.players
    }

    override fun removePlayer(player: Player): List<Player> {
        logger.d("remove $player")
        playerDataSource.removePlayer(player)
        return playerDataSource.players
    }

    private fun generatePlayer(players: List<Player>): Player {
        return Player(id = uniqueIdForPlayer(players), name = getUniqueNameForPlayer(players))
    }

    private fun getUniqueNameForPlayer(players: List<Player>): String {
        var unique = false
        var pickedNumber = 0
        if (players.isEmpty()) {
            return names[pickedNumber]
        }
        while (!unique) {
            val rand = Random()
            pickedNumber = rand.nextInt(names.size)
            var founded = false
            for (player in players) {
                if (player.name.contains(names[pickedNumber], true)) {
                    founded = true
                    continue
                }
            }
            if (!founded) {
                unique = true
            }
        }
        return names[pickedNumber]
    }


    private fun uniqueIdForPlayer(players: List<Player>): Int {
        var id = 0
        if (players.isEmpty()) {
            return id
        }
        id = players.last().id + 1
        return id
    }

}
