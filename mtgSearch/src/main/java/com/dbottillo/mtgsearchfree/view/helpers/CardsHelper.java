package com.dbottillo.mtgsearchfree.view.helpers;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CardsHelper {

    public static void filterCards(CardFilter cardFilter, List<MTGCard> input, ArrayList<MTGCard> output) {
        LOG.d();
        filterCards(cardFilter, null, input, output);
    }

    public static void filterCards(CardFilter cardFilter, SearchParams searchParams, List<MTGCard> input, ArrayList<MTGCard> output) {
        LOG.d();
        if (cardFilter == null) {
            return;
        }
        for (MTGCard card : input) {
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
                output.add(card);
            }
        }
    }

    public static void sortCards(boolean wubrgSort, List<MTGCard> cards) {
        LOG.d();
        if (wubrgSort) {
            Collections.sort(cards, new Comparator<MTGCard>() {
                @Override
                public int compare(MTGCard lhs, MTGCard rhs) {
                    return lhs.compareTo(rhs);
                }
            });
        } else {
            Collections.sort(cards, new Comparator<MTGCard>() {
                @Override
                public int compare(MTGCard lhs, MTGCard rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }
    }
}
