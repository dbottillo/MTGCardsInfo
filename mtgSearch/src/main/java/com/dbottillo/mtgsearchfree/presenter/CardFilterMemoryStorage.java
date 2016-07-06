package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.CardFilter;

public final class CardFilterMemoryStorage {

    private CardFilterMemoryStorage() {

    }

    public static boolean init = false;
    public static CardFilter filter = new CardFilter();
}
