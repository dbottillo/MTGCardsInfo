package com.dbottillo.mtgsearchfree.communication.events;

import com.dbottillo.mtgsearchfree.resources.MTGCard;

import java.util.ArrayList;

public class CardsEvent extends BaseEvent<ArrayList<MTGCard>> {

    public CardsEvent(ArrayList<MTGCard> result) {
        this.result = result;
    }
}
