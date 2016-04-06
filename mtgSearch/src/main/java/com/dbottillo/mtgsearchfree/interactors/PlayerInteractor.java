package com.dbottillo.mtgsearchfree.interactors;


import com.dbottillo.mtgsearchfree.model.Player;

import java.util.List;

import rx.Observable;

public interface PlayerInteractor {

    Observable<List<Player>> load();

    Observable<List<Player>> addPlayer(Player player);

    Observable<List<Player>> editPlayer(Player player);

    Observable<List<Player>> editPlayers(List<Player> players);

    Observable<List<Player>> removePlayer(Player player);
}