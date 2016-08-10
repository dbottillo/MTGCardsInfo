package com.dbottillo.mtgsearchfree.interactors;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class DecksInteractorImpl implements DecksInteractor {

    DecksStorage storage;
    FileUtil fileUtil;

    @Inject
    public DecksInteractorImpl(DecksStorage storage, FileUtil fileUtil) {
        LOG.d("created");
        this.storage = storage;
        this.fileUtil = fileUtil;
    }

    public Observable<List<Deck>> load() {
        LOG.d("loadSet decks");
        return Observable.just(storage.load());
    }

    @Override
    public Observable<List<MTGCard>> loadDeck(Deck deck) {
        LOG.d("loadSet " + deck.toString());
        return Observable.just(storage.loadDeck(deck));
    }

    @Override
    public Observable<List<Deck>> addDeck(String name) {
        LOG.d("create deck with name: " + name);
        return Observable.just(storage.addDeck(name));
    }

    @Override
    public Observable<List<Deck>> deleteDeck(Deck deck) {
        LOG.d("delete " + deck.toString());
        return Observable.just(storage.deleteDeck(deck));
    }

    @Override
    public Observable<List<MTGCard>> editDeck(Deck deck, String name) {
        LOG.d("edit " + deck.toString() + " with name: " + name);
        return Observable.just(storage.editDeck(deck, name));
    }

    @Override
    public Observable<List<MTGCard>> addCard(Deck deck, MTGCard card, int quantity) {
        LOG.d("add " + quantity + " " + card.toString() + " to deck: " + deck);
        return Observable.just(storage.addCard(deck, card, quantity));
    }

    @Override
    public Observable<List<MTGCard>> addCard(String name, MTGCard card, int quantity) {
        LOG.d("add " + quantity + " " + card.toString() + " to new deck with name: " + name);
        return Observable.just(storage.addCard(name, card, quantity));
    }

    @Override
    public Observable<List<MTGCard>> removeCard(Deck deck, MTGCard card) {
        LOG.d("remove " + card.toString() + " from deck: " + deck);
        return Observable.just(storage.removeCard(deck, card));
    }

    @Override
    public Observable<List<MTGCard>> removeAllCard(Deck deck, MTGCard card) {
        LOG.d("remove all " + card.toString() + " from deck: " + deck);
        return Observable.just(storage.removeAllCard(deck, card));
    }

    @Override
    public Observable<List<Deck>> importDeck(Uri uri) {
        LOG.d("import " + uri.toString());
        return Observable.just(storage.importDeck(uri));
    }

    @Override
    public Observable<Boolean> exportDeck(Deck deck, List<MTGCard> cards) {
        return Observable.just(fileUtil.downloadDeckToSdCard(deck, cards));
    }

}
