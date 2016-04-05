package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;

public class DecksStorage {

    private Context context;

    public DecksStorage(Context context) {
        LOG.d("created");
        this.context = context;
    }

    public ArrayList<Deck> load() {
        LOG.d();
        return DeckDataSource.getDecks(CardsInfoDbHelper.getInstance(context).getReadableDatabase());
    }

    public ArrayList<Deck> addDeck(String name) {
        LOG.d("add " + name);
        DeckDataSource.addDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), name);
        return load();
    }

    public ArrayList<Deck> deleteDeck(Deck deck) {
        LOG.d("delete " + deck);
        DeckDataSource.deleteDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deck);
        return load();
    }

    public ArrayList<MTGCard> loadDeck(Deck deck) {
        LOG.d("loadSet " + deck);
        return DeckDataSource.getCards(CardsInfoDbHelper.getInstance(context).getReadableDatabase(), deck);
    }

    public ArrayList<MTGCard> editDeck(Deck deck, String name) {
        LOG.d("edit " + deck + " with " + name);
        DeckDataSource.renameDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deck.getId(), name);
        return loadDeck(deck);
    }

    public ArrayList<MTGCard> addCard(Deck deck, MTGCard card, int quantity) {
        LOG.d("add " + quantity + " " + card + " to " + deck);
        DeckDataSource.addCardToDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deck.getId(), card, quantity, card.isSideboard());
        return loadDeck(deck);
    }

    public ArrayList<MTGCard> addCard(String name, MTGCard card, int quantity) {
        LOG.d("add " + quantity + " " + card + " with new deck name " + name);
        long deckId = DeckDataSource.addDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), name);
        DeckDataSource.addCardToDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deckId, card, quantity, card.isSideboard());
        return DeckDataSource.getCards(CardsInfoDbHelper.getInstance(context).getReadableDatabase(), deckId);
    }

    public ArrayList<MTGCard> removeCard(Deck deck, MTGCard card) {
        LOG.d("remove " + card + " from " + deck);
        DeckDataSource.addCardToDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deck.getId(), card, -1, card.isSideboard());
        return loadDeck(deck);
    }

    public ArrayList<MTGCard> removeAllCard(Deck deck, MTGCard card) {
        LOG.d("remove all " + card + " from " + deck);
        DeckDataSource.removeCardFromDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deck.getId(), card, card.isSideboard());
        return loadDeck(deck);
    }
}

