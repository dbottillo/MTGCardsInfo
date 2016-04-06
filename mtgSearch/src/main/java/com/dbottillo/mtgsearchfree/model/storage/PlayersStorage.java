package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.PlayerDataSource;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.List;

public class PlayersStorage {

    private Context context;

    public PlayersStorage(Context context) {

        LOG.d("created");
        this.context = context;
    }

    public List<Player> load() {
        LOG.d();
        return PlayerDataSource.getPlayers(CardsInfoDbHelper.getInstance(context).getReadableDatabase());
    }

    public List<Player> addPlayer(Player player) {
        LOG.d("add " + player);
        CardsInfoDbHelper helper = CardsInfoDbHelper.getInstance(context);
        PlayerDataSource.savePlayer(helper.getWritableDatabase(), player);
        return load();
    }

    public List<Player> editPlayer(Player player) {
        LOG.d("edit " + player);
        CardsInfoDbHelper helper = CardsInfoDbHelper.getInstance(context);
        PlayerDataSource.savePlayer(helper.getWritableDatabase(), player);
        return load();
    }

    public List<Player> editPlayers(List<Player> players) {
        LOG.d("update " + players);
        CardsInfoDbHelper helper = CardsInfoDbHelper.getInstance(context);
        for (Player player : players) {
            PlayerDataSource.savePlayer(helper.getWritableDatabase(), player);
        }
        return load();
    }

    public List<Player> removePlayer(Player player) {
        LOG.d("remove " + player);
        CardsInfoDbHelper helper = CardsInfoDbHelper.getInstance(context);
        PlayerDataSource.removePlayer(helper.getWritableDatabase(), player);
        return load();
    }
}
