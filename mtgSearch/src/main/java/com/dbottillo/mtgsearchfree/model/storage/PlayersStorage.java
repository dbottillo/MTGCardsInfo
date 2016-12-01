package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.database.PlayerDataSource;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.List;

public class PlayersStorage {

    private PlayerDataSource playerDataSource;

    public PlayersStorage(PlayerDataSource playerDataSource) {
        LOG.d("created");
        this.playerDataSource = playerDataSource;
    }

    public List<Player> load() {
        LOG.d();
        return playerDataSource.getPlayers();
    }

    public List<Player> addPlayer(Player player) {
        LOG.d("add " + player);
        playerDataSource.savePlayer(player);
        return load();
    }

    public List<Player> editPlayer(Player player) {
        LOG.d("edit " + player);
        playerDataSource.savePlayer(player);
        return load();
    }

    public List<Player> editPlayers(List<Player> players) {
        LOG.d("update " + players);
        for (Player player : players) {
            playerDataSource.savePlayer(player);
        }
        return load();
    }

    public List<Player> removePlayer(Player player) {
        LOG.d("remove " + player);
        playerDataSource.removePlayer(player);
        return load();
    }
}
