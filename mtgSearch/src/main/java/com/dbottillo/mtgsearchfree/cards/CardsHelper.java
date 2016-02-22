package com.dbottillo.mtgsearchfree.cards;

import com.dbottillo.mtgsearchfree.helper.FilterHelper;
import com.dbottillo.mtgsearchfree.resources.CardFilter;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.search.SearchParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class CardsHelper {

    private CardsHelper() {

    }

    public static void filterCards(CardFilter cardFilter, SearchParams searchParams, ArrayList<MTGCard> input, ArrayList<MTGCard> output) {
        for (MTGCard card : input) {
            boolean toAdd = false;
            if (searchParams == null) {
                if (card.isWhite() && cardFilter.getWhite()) {
                    toAdd = true;
                }
                if (card.isBlue() && cardFilter.getBlue()) {
                    toAdd = true;
                }
                if (card.isBlack() && cardFilter.getBlack()) {
                    toAdd = true;
                }
                if (card.isRed() && cardFilter.getRed()) {
                    toAdd = true;
                }
                if (card.isGreen() && cardFilter.getGreen()) {
                    toAdd = true;
                }
                if (card.isLand() && cardFilter.getLand()) {
                    toAdd = true;
                }
                if (card.isArtifact() && cardFilter.getArtifact()) {
                    toAdd = true;
                }
                if (card.isEldrazi() && cardFilter.getEldrazi()) {
                    toAdd = true;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_COMMON)
                        && !cardFilter.getCommon()) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_UNCOMMON)
                        && !cardFilter.getUncommon()) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_RARE)
                        && !cardFilter.getRare()) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_MYHTIC)
                        && !cardFilter.getMythic()) {
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

    public static void sortCards(boolean wubrgSort, ArrayList<MTGCard> cards) {
        if (wubrgSort) {
            Collections.sort(cards, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    MTGCard card = (MTGCard) o1;
                    MTGCard card2 = (MTGCard) o2;
                    return card.compareTo(card2);
                }
            });
        } else {
            Collections.sort(cards, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    MTGCard card = (MTGCard) o1;
                    MTGCard card2 = (MTGCard) o2;
                    return card.getName().compareTo(card2.getName());
                }
            });
        }
    }

}
