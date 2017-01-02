package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.Logger;

import java.util.List;

import rx.Observable;

public class PlayerInteractorImpl implements PlayerInteractor {

    private final PlayersStorage storage;
    private final Logger logger;

    public PlayerInteractorImpl(PlayersStorage storage, Logger logger) {
        this.logger = logger;
        this.storage = storage;
        logger.d("created");
    }

    public Observable<List<Player>> load() {
        logger.d("loadSet");
        return Observable.just(storage.load());
    }

    @Override
    public Observable<List<Player>> addPlayer(Player player) {
        logger.d("add " + player);
        return Observable.just(storage.addPlayer(player));
    }

    @Override
    public Observable<List<Player>> editPlayer(Player player) {
        logger.d("edit " + player);
        return Observable.just(storage.editPlayer(player));
    }

    @Override
    public Observable<List<Player>> editPlayers(List<Player> players) {
        logger.d("update players " + players.toString());
        return Observable.just(storage.editPlayers(players));
    }

    @Override
    public Observable<List<Player>> removePlayer(Player player) {
        logger.d("remove " + player);
        return Observable.just(storage.removePlayer(player));
    }

}
