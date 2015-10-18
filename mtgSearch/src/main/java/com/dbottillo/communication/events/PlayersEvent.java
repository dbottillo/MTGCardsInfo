package com.dbottillo.communication.events;

import com.dbottillo.resources.Player;

import java.util.ArrayList;

public class PlayersEvent extends BaseEvent<ArrayList<Player>> {

    public PlayersEvent(ArrayList<Player> result) {
        this.result = result;
    }
}
