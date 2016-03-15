package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.resources.MTGCard;

import java.util.ArrayList;

public interface CardsView extends BasicView {

    void cardLoaded(CardsBucket bucket);

    void luckyCardsLoaded(ArrayList<MTGCard> cards);

    void favIdLoaded(int[] favourites);
}