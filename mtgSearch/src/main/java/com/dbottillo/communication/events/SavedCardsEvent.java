package com.dbottillo.communication.events;

import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

public class SavedCardsEvent extends BaseEvent<ArrayList<MTGCard>> {

    public SavedCardsEvent(ArrayList<MTGCard> result) {
        this.result = result;
    }
}
