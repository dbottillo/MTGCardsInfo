package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.PlayerDataSource;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.List;

public class PlayersStorage {

    private CardsInfoDbHelper helper;

    public PlayersStorage(CardsInfoDbHelper helper) {

        LOG.d("created");
        this.helper = helper;
    }

    public List<Player> load() {
        LOG.d();
        return helper.loadPlayers();
    }

    public List<Player> addPlayer(Player player) {
        LOG.d("add " + player);
        helper.savePlayer(player);
        return load();
    }

    public List<Player> editPlayer(Player player) {
        LOG.d("edit " + player);
        helper.editPlayer(player);
        return load();
    }

    public List<Player> editPlayers(List<Player> players) {
        LOG.d("update " + players);
        for (Player player : players) {
            helper.editPlayer(player);
        }
        return load();
    }

    public List<Player> removePlayer(Player player) {
        LOG.d("remove " + player);
        helper.removePlayer(player);
        return load();
    }
}
