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

public final class CardsHelper {

    private CardsPreferences cardsPreferences;

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
        List<MTGCard> allCards = bucket.getCards();
        ArrayList<MTGCard> filteredCards = new ArrayList<>();
        CardsBucket filteredBucket = new CardsBucket();
        filteredBucket.setKey(bucket.getKey());
        if (cardFilter == null) {
            filteredBucket.setCards(filteredCards);
            return filteredBucket;
        }
        for (MTGCard card : allCards) {
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
        filteredBucket.setCards(filteredCards);
        return filteredBucket;
    }

    public void sortCards(CardsBucket bucket) {
        LOG.d();
        boolean wubrgSort = cardsPreferences.isSortWUBRG();
        if (wubrgSort) {
            Collections.sort(bucket.getCards(), new Comparator<MTGCard>() {
                @Override
                public int compare(MTGCard lhs, MTGCard rhs) {
                    return lhs.compareTo(rhs);
                }
            });
        } else {
            Collections.sort(bucket.getCards(), new Comparator<MTGCard>() {
                @Override
                public int compare(MTGCard lhs, MTGCard rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }
    }
}
