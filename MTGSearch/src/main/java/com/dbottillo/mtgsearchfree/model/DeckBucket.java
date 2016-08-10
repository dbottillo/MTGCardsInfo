package com.dbottillo.mtgsearchfree.model;

import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.List;

public class DeckBucket extends CardsBucket {

    private ArrayList<MTGCard> creatures = new ArrayList<>();
    private ArrayList<MTGCard> instantAndSorceries = new ArrayList<>();
    private ArrayList<MTGCard> other = new ArrayList<>();
    private ArrayList<MTGCard> lands = new ArrayList<>();
    private ArrayList<MTGCard> side = new ArrayList<>();


    public ArrayList<MTGCard> getCreatures() {
        return creatures;
    }

    public ArrayList<MTGCard> getInstantAndSorceries() {
        return instantAndSorceries;
    }

    public ArrayList<MTGCard> getOther() {
        return other;
    }

    public ArrayList<MTGCard> getLands() {
        return lands;
    }

    public ArrayList<MTGCard> getSide() {
        return side;
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

    @Override
    public String toString() {
        return "DeckBucket: [nCreatures:" + creatures.size() + ", nLands" + lands.size()
                + ", nInstantAndSorceries:" + instantAndSorceries.size() + ", nOther:" + other.size() + "]";
    }

    public int numberOfCards() {
        List<MTGCard> cards = getCards();
        int total = 0;
        for (MTGCard card : cards) {
            total += card.getQuantity();
        }
        return total;
    }

    public int numberOfUniqueCards() {
        return creatures.size() + instantAndSorceries.size() + other.size() + lands.size() + side.size();
    }

    public int numberOfCardsWithoutSideboard() {
        return numberOfCards() - numberOfCardsInSideboard();
    }

    public int numberOfCardsInSideboard() {
        return totalQuantityOfList(side);
    }

    public int numberOfUniqueCardsInSideboard() {
        return side.size();
    }

    public int getNumberOfUniqueCreatures() {
        return creatures.size();
    }

    public int getNumberOfCreatures() {
        return totalQuantityOfList(creatures);
    }

    public int getNumberOfUniqueInstantAndSorceries() {
        return instantAndSorceries.size();
    }

    public int getNumberOfInstantAndSorceries() {
        return totalQuantityOfList(instantAndSorceries);
    }

    public int getNumberOfUniqueLands() {
        return lands.size();
    }

    public int getNumberOfLands() {
        return totalQuantityOfList(lands);
    }

    public int getNumberOfUniqueOther() {
        return other.size();
    }

    public int getNumberOfOther() {
        return totalQuantityOfList(other);
    }

    private static int totalQuantityOfList(List<MTGCard> list){
        int total = 0;
        for (MTGCard card : list) {
            total += card.getQuantity();
        }
        return total;
    }
}
