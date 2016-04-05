package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;

import rx.Observable;

public class DecksInteractorImpl implements DecksInteractor {

    DecksStorage storage;

    public DecksInteractorImpl(DecksStorage storage) {
        LOG.d("created");
        this.storage = storage;
    }

    public Observable<ArrayList<Deck>> load() {
        LOG.d("load decks");
        return Observable.just(storage.load());
    }

    @Override
    public Observable<ArrayList<MTGCard>> loadDeck(Deck deck) {
        LOG.d("load " + deck.toString());
        return Observable.just(storage.loadDeck(deck));
    }

    @Override
    public Observable<ArrayList<Deck>> addDeck(String name) {
        LOG.d("create deck with name: " + name);
        return Observable.just(storage.addDeck(name));
    }

    @Override
    public Observable<ArrayList<Deck>> deleteDeck(Deck deck) {
        LOG.d("delete " + deck.toString());
        return Observable.just(storage.deleteDeck(deck));
    }

    @Override
    public Observable<ArrayList<MTGCard>> editDeck(Deck deck, String name) {
        LOG.d("edit " + deck.toString() + " with name: " + name);
        return Observable.just(storage.editDeck(deck, name));
    }

    @Override
    public Observable<ArrayList<MTGCard>> addCard(Deck deck, MTGCard card, int quantity) {
        LOG.d("add " + quantity + " " + card.toString() + " to deck: " + deck);
        return Observable.just(storage.addCard(deck, card, quantity));
    }

    @Override
    public Observable<ArrayList<MTGCard>> addCard(String name, MTGCard card, int quantity) {
        LOG.d("add " + quantity + " " + card.toString() + " to new deck with name: " + name);
        return Observable.just(storage.addCard(name, card, quantity));
    }

    @Override
    public Observable<ArrayList<MTGCard>> removeCard(Deck deck, MTGCard card) {
        LOG.d("remove " + card.toString() + " from deck: " + deck);
        return Observable.just(storage.removeCard(deck, card));
    }

    @Override
    public Observable<ArrayList<MTGCard>> removeAllCard(Deck deck, MTGCard card) {
        LOG.d("remove all " + card.toString() + " from deck: " + deck);
        return Observable.just(storage.removeAllCard(deck, card));
    }

}
