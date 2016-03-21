package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;

import java.util.ArrayList;

import rx.Observable;

public class PlayerInteractorImpl implements PlayerInteractor {

    PlayersStorage storage;

    public PlayerInteractorImpl(PlayersStorage storage) {
        this.storage = storage;
    }

    public Observable<ArrayList<Player>> load() {
        return Observable.just(storage.load());
    }

    @Override
    public Observable<ArrayList<Player>> addPlayer(Player player) {
        return Observable.just(storage.addPlayer(player));
    }

    @Override
    public Observable<ArrayList<Player>> editPlayer(Player player) {
        return Observable.just(storage.editPlayer(player));
    }

    @Override
    public Observable<ArrayList<Player>> editPlayers(ArrayList<Player> players) {
        return Observable.just(storage.editPlayers(players));
    }

    @Override
    public Observable<ArrayList<Player>> removePlayer(Player player) {
        return Observable.just(storage.removePlayer(player));
    }

}
