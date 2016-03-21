package com.dbottillo.mtgsearchfree.communication.events;

import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.util.ArrayList;

public class SavedCardsEvent extends BaseEvent<ArrayList<MTGCard>> {

    public SavedCardsEvent(ArrayList<MTGCard> result) {
        this.result = result;
    }
}
