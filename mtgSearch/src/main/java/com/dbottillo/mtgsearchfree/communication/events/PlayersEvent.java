package com.dbottillo.mtgsearchfree.communication.events;

import com.dbottillo.mtgsearchfree.resources.Player;

import java.util.ArrayList;

public class PlayersEvent extends BaseEvent<ArrayList<Player>> {

    public PlayersEvent(ArrayList<Player> result) {
        this.result = result;
    }
}
