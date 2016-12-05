package com.dbottillo.mtgsearchfree.util;

import com.dbottillo.mtgsearchfree.model.CardProperties;
import com.dbottillo.mtgsearchfree.model.Deck;

import java.util.List;
import java.util.Locale;

public final class StringUtil {

    private StringUtil() {

    }

    public static boolean contains(String input, String target) {
        if (input == null || input.length() == 0 || target == null || target.length() == 0) {
            return false;
        }
        return input.toLowerCase(Locale.ENGLISH).contains(target.toLowerCase(Locale.ENGLISH));
    }

    static String clearDeckName(Deck deck) {
        return deck.getName().replaceAll("\\s+", "").toLowerCase(Locale.getDefault());
    }

    public static String joinListOfColors(List<Integer> list, String separator) {
        StringBuilder joined = new StringBuilder("");
        if (list.size() == 0) {
            return joined.toString();
        }
        for (int i = 0; i < list.size(); i++) {
            int value = list.get(i);
            String color = CardProperties.COLOR.getStringFromNumber(value);
            joined.append(color);
            if (i < list.size() - 1) {
                joined.append(separator);
            }
        }
        return joined.toString();
    }

    public static String joinListOfStrings(List<String> list, String separator) {
        StringBuilder joined = new StringBuilder("");
        if (list.size() == 0) {
            return joined.toString();
        }
        for (int i = 0; i < list.size(); i++) {
            String value = list.get(i);
            joined.append(value);
            if (i < list.size() - 1) {
                joined.append(separator);
            }
        }
        return joined.toString();
    }
}
