package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;

import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.util.ArrayList;

public class DecksStorage {

    private Context context;

    public DecksStorage(Context context) {
        this.context = context;
    }

    public ArrayList<Deck> load() {
        return DeckDataSource.getDecks(CardsInfoDbHelper.getInstance(context).getReadableDatabase());
    }

    public ArrayList<Deck> addDeck(String name) {
        DeckDataSource.addDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), name);
        return load();
    }

    public ArrayList<Deck> deleteDeck(Deck deck) {
        DeckDataSource.deleteDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deck);
        return load();
    }

    public ArrayList<MTGCard> loadDeck(Deck deck) {
        return DeckDataSource.getCards(CardsInfoDbHelper.getInstance(context).getReadableDatabase(), deck);
    }

    public ArrayList<MTGCard> editDeck(Deck deck, String name) {
        DeckDataSource.renameDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deck.getId(), name);
        return loadDeck(deck);
    }

    public ArrayList<MTGCard> addCard(Deck deck, MTGCard card, int quantity) {
        DeckDataSource.addCardToDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deck.getId(), card, quantity, card.isSideboard());
        return loadDeck(deck);
    }

    public ArrayList<MTGCard> addCard(String name, MTGCard card, int quantity) {
        long deckId = DeckDataSource.addDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), name);
        DeckDataSource.addCardToDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deckId, card, quantity, card.isSideboard());
        return DeckDataSource.getCards(CardsInfoDbHelper.getInstance(context).getReadableDatabase(), deckId);
    }

    public ArrayList<MTGCard> removeCard(Deck deck, MTGCard card) {
        DeckDataSource.addCardToDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deck.getId(), card, -1,  card.isSideboard());
        return loadDeck(deck);
    }

    public ArrayList<MTGCard>  removeAllCard(Deck deck, MTGCard card) {
        DeckDataSource.removeCardFromDeck(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), deck.getId(), card, card.isSideboard());
        return loadDeck(deck);
    }
}

