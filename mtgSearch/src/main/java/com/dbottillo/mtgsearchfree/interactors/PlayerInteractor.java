package com.dbottillo.mtgsearchfree.interactors;


import com.dbottillo.mtgsearchfree.model.Player;

import java.util.ArrayList;

import rx.Observable;

public interface PlayerInteractor {

    Observable<ArrayList<Player>> load();

    Observable<ArrayList<Player>> addPlayer(Player player);

    Observable<ArrayList<Player>> editPlayer(Player player);

    Observable<ArrayList<Player>> removePlayer(Player player);
}