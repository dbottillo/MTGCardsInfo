package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.model.CardsBucket;

public interface CardsView extends BasicView {

    void cardLoaded(CardsBucket bucket);

    void favIdLoaded(int[] favourites);
}