package com.dbottillo.mtgsearchfree.model;

import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.List;

public class DeckBucket extends CardsBucket {

    public ArrayList<MTGCard> creatures = new ArrayList<>();
    public ArrayList<MTGCard> instantAndSorceries = new ArrayList<>();
    public ArrayList<MTGCard> other = new ArrayList<>();
    public ArrayList<MTGCard> lands = new ArrayList<>();
    public ArrayList<MTGCard> side = new ArrayList<>();

    public int size() {
        return creatures.size() + instantAndSorceries.size() + other.size() + lands.size() + side.size();
    }

    @Override
    public void setCards(List<MTGCard> cards) {
        for (MTGCard card : cards) {
            if (card.isSideboard()) {
                side.add(card);
            } else if (card.isLand()) {
                lands.add(card);
            } else if (card.getTypes().contains("Creature")) {
                creatures.add(card);
            } else if (card.getTypes().contains("Instant") || card.getTypes().contains("Sorcery")) {
                instantAndSorceries.add(card);
            } else {
                other.add(card);
            }
        }
    }

    @Override
    public List<MTGCard> getCards() {
        LOG.d("deck of bucket " + key + " requested");
        ArrayList<MTGCard> cards = new ArrayList<>(creatures);
        cards.addAll(instantAndSorceries);
        cards.addAll(other);
        cards.addAll(lands);
        cards.addAll(side);
        return cards;
    }

    public int sizeNoSideboard() {
        return creatures.size() + instantAndSorceries.size() + other.size() + lands.size();
    }

    public int sizeSideBoard() {
        return side.size();
    }

    @Override
    public String toString() {
        return "DeckBucket: [nCreatures:" + creatures.size() + ", nLands" + lands.size() +
                ", nInstantAndSorceries:" + instantAndSorceries.size() + ", nOther:" + other.size() + "]";
    }

}
