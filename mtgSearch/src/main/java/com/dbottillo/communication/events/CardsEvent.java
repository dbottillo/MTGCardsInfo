package com.dbottillo.communication.events;

import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

public class CardsEvent extends BaseEvent<ArrayList<MTGCard>> {

    public CardsEvent(ArrayList<MTGCard> result) {
        this.result = result;
    }
}
