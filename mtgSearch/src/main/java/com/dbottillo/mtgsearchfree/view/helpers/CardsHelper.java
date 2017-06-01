package com.dbottillo.mtgsearchfree.view.helpers;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class CardsHelper {

    private CardsPreferences cardsPreferences;

    public CardsHelper(){

    }

    @Inject
    public CardsHelper(CardsPreferences cardsPreferences) {
        this.cardsPreferences = cardsPreferences;
    }

    public CardsBucket filterCards(CardFilter cardFilter, CardsBucket bucket) {
        LOG.d();
        return filterCards(cardFilter, null, bucket);
    }

    public CardsBucket filterCards(CardFilter cardFilter, SearchParams searchParams, CardsBucket bucket) {
        LOG.d();
        CardsBucket filteredBucket = new CardsBucket();
        filteredBucket.setKey(bucket.getKey());
        filteredBucket.setCards(filterCards(cardFilter, searchParams, bucket.getCards()));
        return filteredBucket;
    }

    public List<MTGCard> filterCards(CardFilter cardFilter, List<MTGCard> cards) {
        LOG.d();
        return filterCards(cardFilter, null, cards);
    }

    public List<MTGCard> filterCards(CardFilter cardFilter, SearchParams searchParams, List<MTGCard> cards){
        ArrayList<MTGCard> filteredCards = new ArrayList<>();
        if (cardFilter == null) {
            return cards;
        }
        for (MTGCard card : cards) {
            boolean toAdd = false;
            if (searchParams == null) {
                if (card.isWhite() && cardFilter.white) {
                    toAdd = true;
                }
                if (card.isBlue() && cardFilter.blue) {
                    toAdd = true;
                }
                if (card.isBlack() && cardFilter.black) {
                    toAdd = true;
                }
                if (card.isRed() && cardFilter.red) {
                    toAdd = true;
                }
                if (card.isGreen() && cardFilter.green) {
                    toAdd = true;
                }
                if (card.isLand() && cardFilter.land) {
                    toAdd = true;
                }
                if (card.isArtifact() && cardFilter.artifact) {
                    toAdd = true;
                }
                if (card.isEldrazi() && cardFilter.eldrazi) {
                    toAdd = true;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_COMMON)
                        && !cardFilter.common) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_UNCOMMON)
                        && !cardFilter.uncommon) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_RARE)
                        && !cardFilter.rare) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_MYHTIC)
                        && !cardFilter.mythic) {
                    toAdd = false;
                }
            } else {
                // for search, filter don't apply
                toAdd = true;
            }

            if (toAdd) {
                filteredCards.add(card);
            }
        }
        sortCards(cardFilter.sortWUBGR, filteredCards);
        return filteredCards;
    }

    private void sortCards(boolean wubrgSort, List<MTGCard> cards){
        if (wubrgSort) {
            sortWUBGRCards(cards);
        } else {
            sortAZCards(cards);
        }
    }

    public void sortWUBGRCards(List<MTGCard> cards){
        Collections.sort(cards, new Comparator<MTGCard>() {
            @Override
            public int compare(MTGCard lhs, MTGCard rhs) {
                return lhs.compareTo(rhs);
            }
        });
    }

    public void sortAZCards(List<MTGCard> cards){
        Collections.sort(cards, new Comparator<MTGCard>() {
            @Override
            public int compare(MTGCard lhs, MTGCard rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
    }
}
