package com.dbottillo.mtgsearchfree.util;

import com.dbottillo.mtgsearchfree.model.Deck;

import java.util.Locale;

public class StringUtil {

    public static boolean contains(String input, String target) {
        return input.toLowerCase(Locale.ENGLISH).contains(target.toLowerCase(Locale.ENGLISH));
    }

    public static String clearDeckName(Deck deck) {
        return deck.getName().replaceAll("\\s+", "").toLowerCase(Locale.getDefault());
    }
}
