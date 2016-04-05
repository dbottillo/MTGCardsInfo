package com.dbottillo.mtgsearchfree.model;

import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;

public class CardsBucket {

    String key;
    ArrayList<MTGCard> cards;

    public CardsBucket(MTGSet set, ArrayList<MTGCard> cards) {
        LOG.d("creating bucket with key: " + set.getName());
        this.key = set.getName();
        this.cards = cards;
    }

    public CardsBucket(String key, ArrayList<MTGCard> cards) {
        LOG.d("creating bucket with key: " + key);
        this.key = key;
        this.cards = cards;
    }

    public ArrayList<MTGCard> getCards() {
        LOG.d("cards of bucket " + key + " requested");
        return cards;
    }

    public boolean isValid(String otherKey) {
        LOG.d("current bucket contains " + key + " an key required is: " + otherKey);
        return key.equals(otherKey);
    }

    public void setCards(ArrayList<MTGCard> cards) {
        this.cards = cards;
    }

    public String getKey() {
        return key;
    }
}
