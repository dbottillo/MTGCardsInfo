package com.dbottillo.mtgsearchfree.model;

import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.resources.MTGSet;

import java.util.ArrayList;

public class CardsBucket {

    MTGSet set;
    ArrayList<MTGCard> cards;

    public CardsBucket(MTGSet set, ArrayList<MTGCard> cards) {
        this.set = set;
        this.cards = cards;
    }

    public CardsBucket(String search, ArrayList<MTGCard> cards) {
        this.set = new MTGSet(-1);
        this.cards = cards;
        this.set.setName(search);
    }

    public boolean isValid(String key) {
        return set.getName().equals(key);
    }
}
