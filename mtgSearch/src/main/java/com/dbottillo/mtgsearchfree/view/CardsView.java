package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.DeckBucket;

public interface CardsView extends BasicView {

    void cardLoaded(CardsBucket bucket);

    void deckLoaded(DeckBucket bucket);

    void favIdLoaded(int[] favourites);
}