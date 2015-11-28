package com.dbottillo.cards;

import android.content.SharedPreferences;

import com.dbottillo.base.DBFragment;
import com.dbottillo.helper.FilterHelper;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.search.SearchParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class CardsHelper {

    private CardsHelper() {

    }

    public static void filterCards(SharedPreferences sharedPreferences, SearchParams searchParams, ArrayList<MTGCard> input, ArrayList<MTGCard> output) {
        for (MTGCard card : input) {
            boolean toAdd = false;
            if (searchParams == null) {
                if (card.isWhite() && sharedPreferences.getBoolean(FilterHelper.FILTER_WHITE, true)) {
                    toAdd = true;
                }
                if (card.isBlue() && sharedPreferences.getBoolean(FilterHelper.FILTER_BLUE, true)) {
                    toAdd = true;
                }
                if (card.isBlack() && sharedPreferences.getBoolean(FilterHelper.FILTER_BLACK, true)) {
                    toAdd = true;
                }
                if (card.isRed() && sharedPreferences.getBoolean(FilterHelper.FILTER_RED, true)) {
                    toAdd = true;
                }
                if (card.isGreen() && sharedPreferences.getBoolean(FilterHelper.FILTER_GREEN, true)) {
                    toAdd = true;
                }
                if (card.isLand() && sharedPreferences.getBoolean(FilterHelper.FILTER_LAND, true)) {
                    toAdd = true;
                }
                if (card.isArtifact() && sharedPreferences.getBoolean(FilterHelper.FILTER_ARTIFACT, true)) {
                    toAdd = true;
                }
                if (card.isEldrazi() && sharedPreferences.getBoolean(FilterHelper.FILTER_ELDRAZI, true)) {
                    toAdd = true;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_COMMON)
                        && !sharedPreferences.getBoolean(FilterHelper.FILTER_COMMON, true)) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_UNCOMMON)
                        && !sharedPreferences.getBoolean(FilterHelper.FILTER_UNCOMMON, true)) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_RARE)
                        && !sharedPreferences.getBoolean(FilterHelper.FILTER_RARE, true)) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_MYHTIC)
                        && !sharedPreferences.getBoolean(FilterHelper.FILTER_MYHTIC, true)) {
                    toAdd = false;
                }
            } else {
                // for search, filter don't apply
                toAdd = true;
            }

            if (toAdd) {
                output.add(card);
            }

            boolean wubrgSort = sharedPreferences.getBoolean(DBFragment.PREF_SORT_WUBRG, true);
            if (wubrgSort) {
                Collections.sort(output, new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        MTGCard card = (MTGCard) o1;
                        MTGCard card2 = (MTGCard) o2;
                        return card.compareTo(card2);
                    }
                });
            } else {
                Collections.sort(output, new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        MTGCard card = (MTGCard) o1;
                        MTGCard card2 = (MTGCard) o2;
                        return card.getName().compareTo(card2.getName());
                    }
                });
            }
        }
    }

}
