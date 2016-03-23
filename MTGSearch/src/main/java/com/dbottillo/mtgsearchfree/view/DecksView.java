package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.util.List;

public interface DecksView extends BasicView {

    void decksLoaded(List<Deck> decks);

    void deckLoaded(List<MTGCard> cards);
}