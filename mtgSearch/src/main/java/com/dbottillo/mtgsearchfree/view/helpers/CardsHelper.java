package com.dbottillo.mtgsearchfree.view.helpers;

import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CardsHelper {

    public static void filterCards(CardFilter cardFilter, CardsBucket bucket) {
        LOG.d();
        filterCards(cardFilter, null, bucket);
    }

    public static void filterCards(CardFilter cardFilter, SearchParams searchParams, CardsBucket bucket) {
        LOG.d();
        List<MTGCard> allCards = bucket.getCards();
        ArrayList<MTGCard> filteredCards = new ArrayList<>();
        if (cardFilter == null) {
            return;
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
        bucket.setCards(filteredCards);
    }

    public static void sortCards(SharedPreferences sharedPreferences, CardsBucket bucket) {
        LOG.d();
        boolean wubrgSort = sharedPreferences.getBoolean(BasicFragment.PREF_SORT_WUBRG, true);
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
