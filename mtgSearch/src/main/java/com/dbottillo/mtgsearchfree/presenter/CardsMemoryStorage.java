package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.CardsBucket;

public final class CardsMemoryStorage {

    private CardsMemoryStorage() {

    }

    public static CardsBucket bucket = null;

    public static int[] favourites = null;

}