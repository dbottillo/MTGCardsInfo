package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;

import rx.Observable;

public class PlayerInteractorImpl implements PlayerInteractor {

    PlayersStorage storage;

    public PlayerInteractorImpl(PlayersStorage storage) {
        LOG.d("created");
        this.storage = storage;
    }

    public Observable<ArrayList<Player>> load() {
        LOG.d("loadSet");
        return Observable.just(storage.load());
    }

    @Override
    public Observable<ArrayList<Player>> addPlayer(Player player) {
        LOG.d("add " + player);
        return Observable.just(storage.addPlayer(player));
    }

    @Override
    public Observable<ArrayList<Player>> editPlayer(Player player) {
        LOG.d("edit " + player);
        return Observable.just(storage.editPlayer(player));
    }

    @Override
    public Observable<ArrayList<Player>> editPlayers(ArrayList<Player> players) {
        LOG.d("update players " + players.toString());
        return Observable.just(storage.editPlayers(players));
    }

    @Override
    public Observable<ArrayList<Player>> removePlayer(Player player) {
        LOG.d("remove " + player);
        return Observable.just(storage.removePlayer(player));
    }

}
