package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.model.Player;

import java.util.ArrayList;

public interface PlayersView extends BasicView {

    void playersLoaded(ArrayList<Player> players);

    void showLoading();
}