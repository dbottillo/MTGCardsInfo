package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.view.PlayersView;

import java.util.List;

public interface PlayerPresenter extends BasicPresenter {

    void init(PlayersView view);

    void loadPlayers();

    void addPlayer();

    void editPlayer(Player player);

    void editPlayers(List<Player> players);

    void removePlayer(Player player);

}