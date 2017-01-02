package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.database.PlayerDataSource;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.Logger;

import java.util.List;

public class PlayersStorage {

    private final PlayerDataSource playerDataSource;
    private final Logger logger;

    public PlayersStorage(PlayerDataSource playerDataSource, Logger logger) {
        this.logger = logger;
        this.playerDataSource = playerDataSource;
        logger.d("created");
    }

    public List<Player> load() {
        logger.d();
        return playerDataSource.getPlayers();
    }

    public List<Player> addPlayer(Player player) {
        logger.d("add " + player);
        playerDataSource.savePlayer(player);
        return load();
    }

    public List<Player> editPlayer(Player player) {
        logger.d("edit " + player);
        playerDataSource.savePlayer(player);
        return load();
    }

    public List<Player> editPlayers(List<Player> players) {
        logger.d("update " + players);
        for (Player player : players) {
            playerDataSource.savePlayer(player);
        }
        return load();
    }

    public List<Player> removePlayer(Player player) {
        logger.d("remove " + player);
        playerDataSource.removePlayer(player);
        return load();
    }
}
