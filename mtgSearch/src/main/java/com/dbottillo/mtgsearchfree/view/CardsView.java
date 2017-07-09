package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.ui.BasicView;

public interface CardsView extends BasicView {

    void cardsLoaded(CardsBucket bucket);

    void deckLoaded(DeckBucket bucket);

    void favIdLoaded(int[] favourites);

    void cardTypePreferenceChanged(boolean grid);

}