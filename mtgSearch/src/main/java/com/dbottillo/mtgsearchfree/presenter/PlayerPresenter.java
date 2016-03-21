package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.view.PlayersView;

public interface PlayerPresenter extends BasicPresenter {

    void init(PlayersView view);

    void loadPlayers();

    void addPlayer();

    void editPlayer(Player player);

    void removePlayer(Player player);

}