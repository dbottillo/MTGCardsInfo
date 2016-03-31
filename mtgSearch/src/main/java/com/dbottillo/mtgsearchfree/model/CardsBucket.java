package com.dbottillo.mtgsearchfree.model;

import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;

public class CardsBucket {

    MTGSet set;
    ArrayList<MTGCard> cards;

    public CardsBucket(MTGSet set, ArrayList<MTGCard> cards) {
        LOG.e("creating bucket with key: " + set.getName());
        this.set = set;
        this.cards = cards;
    }

    public CardsBucket(String search, ArrayList<MTGCard> cards) {
        this.set = new MTGSet(-1);
        this.cards = cards;
        this.set.setName(search);
    }

    public MTGSet getSet() {
        return set;
    }

    public ArrayList<MTGCard> getCards() {
        return cards;
    }

    public boolean isValid(String key) {
        LOG.e("current bucket contains " + set.getName() + " an key required is: " + key);
        return set.getName().equals(key);
    }

    public void setCards(ArrayList<MTGCard> cards) {
        this.cards = cards;
    }
}
