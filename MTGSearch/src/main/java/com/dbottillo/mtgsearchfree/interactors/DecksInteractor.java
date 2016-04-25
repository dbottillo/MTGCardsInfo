package com.dbottillo.mtgsearchfree.interactors;


import android.net.Uri;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.util.List;

import rx.Observable;

public interface DecksInteractor {

    Observable<List<Deck>> load();

    Observable<List<MTGCard>> loadDeck(Deck deck);

    Observable<List<Deck>> addDeck(String name);

    Observable<List<Deck>> deleteDeck(Deck deck);

    Observable<List<MTGCard>> editDeck(Deck deck, String name);

    Observable<List<MTGCard>> addCard(String name, MTGCard card, int quantity);

    Observable<List<MTGCard>> addCard(Deck deck, MTGCard card, int quantity);

    Observable<List<MTGCard>> removeCard(Deck deck, MTGCard card);

    Observable<List<MTGCard>> removeAllCard(Deck deck, MTGCard card);

    Observable<List<Deck>> importDeck(Uri uri);
}