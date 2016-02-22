package com.dbottillo.mtgsearchfree.communication.events;

import com.dbottillo.mtgsearchfree.resources.MTGCard;

import java.util.ArrayList;

public class RandomCardsEvent extends BaseEvent<ArrayList<MTGCard>> {

    public RandomCardsEvent(ArrayList<MTGCard> result) {
        this.result = result;
    }
}