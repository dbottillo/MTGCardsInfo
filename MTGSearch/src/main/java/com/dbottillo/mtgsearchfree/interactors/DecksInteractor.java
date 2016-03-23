package com.dbottillo.mtgsearchfree.interactors;


import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.util.ArrayList;

import rx.Observable;

public interface DecksInteractor {

    Observable<ArrayList<Deck>> load();

    Observable<ArrayList<MTGCard>> loadDeck(Deck deck);

    Observable<ArrayList<Deck>> addDeck(String name);

    Observable<ArrayList<Deck>> deleteDeck(Deck deck);

    Observable<ArrayList<MTGCard>> editDeck(Deck deck, String name);

    Observable<ArrayList<MTGCard>> addCard(String name, MTGCard card, int quantity);

    Observable<ArrayList<MTGCard>> addCard(Deck deck, MTGCard card, int quantity);

    Observable<ArrayList<MTGCard>> removeCard(Deck deck, MTGCard card);

    Observable<ArrayList<MTGCard>> removeAllCard(Deck deck, MTGCard card);
}