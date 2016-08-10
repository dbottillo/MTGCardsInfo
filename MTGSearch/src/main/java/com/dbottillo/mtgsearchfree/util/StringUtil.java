package com.dbottillo.mtgsearchfree.util;

import com.dbottillo.mtgsearchfree.model.Deck;

import java.util.Locale;

public final class StringUtil {

    private StringUtil(){

    }

    public static boolean contains(String input, String target) {
        return input.toLowerCase(Locale.ENGLISH).contains(target.toLowerCase(Locale.ENGLISH));
    }

    static String clearDeckName(Deck deck) {
        return deck.getName().replaceAll("\\s+", "").toLowerCase(Locale.getDefault());
    }
}
