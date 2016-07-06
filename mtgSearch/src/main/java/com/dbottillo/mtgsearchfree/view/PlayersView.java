package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.model.Player;

import java.util.List;

public interface PlayersView extends BasicView {

    void playersLoaded(List<Player> players);

    void showLoading();
}