package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;

import java.util.ArrayList;

import rx.Observable;

public class DecksInteractorImpl implements DecksInteractor {

    DecksStorage storage;

    public DecksInteractorImpl(DecksStorage storage) {
        this.storage = storage;
    }

    public Observable<ArrayList<Deck>> load() {
        return Observable.just(storage.load());
    }

    @Override
    public Observable<ArrayList<MTGCard>> loadDeck(Deck deck) {
        return Observable.just(storage.loadDeck(deck));
    }

    @Override
    public Observable<ArrayList<Deck>> addDeck(String name) {
        return Observable.just(storage.addDeck(name));
    }

    @Override
    public Observable<ArrayList<Deck>> deleteDeck(Deck deck) {
        return Observable.just(storage.deleteDeck(deck));
    }

    @Override
    public Observable<ArrayList<MTGCard>> editDeck(Deck deck, String name) {
        return Observable.just(storage.editDeck(deck, name));
    }

    @Override
    public Observable<ArrayList<MTGCard>> addCard(Deck deck, MTGCard card, int quantity) {
        return Observable.just(storage.addCard(deck, card, quantity));
    }

    @Override
    public Observable<ArrayList<MTGCard>> addCard(String name, MTGCard card, int quantity) {
        return Observable.just(storage.addCard(name, card, quantity));
    }

    @Override
    public Observable<ArrayList<MTGCard>> removeCard(Deck deck, MTGCard card) {
        return Observable.just(storage.removeCard(deck, card));
    }

    @Override
    public Observable<ArrayList<MTGCard>> removeAllCard(Deck deck, MTGCard card) {
        return Observable.just(storage.removeAllCard(deck, card));
    }

}
