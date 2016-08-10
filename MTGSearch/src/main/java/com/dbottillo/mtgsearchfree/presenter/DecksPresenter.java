package com.dbottillo.mtgsearchfree.presenter;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.view.DecksView;

import java.util.List;

public interface DecksPresenter {

    void init(DecksView view);

    void loadDecks();

    void loadDeck(Deck deck);

    void addDeck(String name);

    void deleteDeck(Deck deck);

    void editDeck(Deck deck, String name);

    void addCardToDeck(Deck deck, MTGCard card, int quantity);

    void addCardToDeck(String name, MTGCard card, int quantity);

    void removeCardFromDeck(Deck deck, MTGCard card);

    void removeAllCardFromDeck(Deck deck, MTGCard card);

    void importDeck(Uri uri);

    void exportDeck(Deck deck, List<MTGCard> cards);
}