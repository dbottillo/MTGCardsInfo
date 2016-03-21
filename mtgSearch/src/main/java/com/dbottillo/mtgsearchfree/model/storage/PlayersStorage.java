package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;

import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.PlayerDataSource;
import com.dbottillo.mtgsearchfree.model.Player;

import java.util.ArrayList;

public class PlayersStorage {

    private Context context;

    public PlayersStorage(Context context) {
        this.context = context;
    }

    public ArrayList<Player> load() {
        return PlayerDataSource.getPlayers(CardsInfoDbHelper.getInstance(context).getReadableDatabase());
    }

    public ArrayList<Player> addPlayer(Player player) {
        CardsInfoDbHelper helper = CardsInfoDbHelper.getInstance(context);
        PlayerDataSource.savePlayer(helper.getWritableDatabase(), player);
        return load();
    }

    public ArrayList<Player> editPlayer(Player player) {
        CardsInfoDbHelper helper = CardsInfoDbHelper.getInstance(context);
        PlayerDataSource.savePlayer(helper.getWritableDatabase(), player);
        return load();
    }

    public ArrayList<Player> editPlayers(ArrayList<Player> players) {
        CardsInfoDbHelper helper = CardsInfoDbHelper.getInstance(context);
        for (Player player : players) {
            PlayerDataSource.savePlayer(helper.getWritableDatabase(), player);
        }
        return load();
    }

    public ArrayList<Player> removePlayer(Player player) {
        CardsInfoDbHelper helper = CardsInfoDbHelper.getInstance(context);
        PlayerDataSource.removePlayer(helper.getWritableDatabase(), player);
        return load();
    }
}
