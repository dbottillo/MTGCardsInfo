package com.dbottillo.mtgsearchfree.interactors;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.Logger;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class DecksInteractorImpl implements DecksInteractor {

    private final DecksStorage storage;
    private final FileUtil fileUtil;
    private final Logger logger;

    @Inject
    public DecksInteractorImpl(DecksStorage storage, FileUtil fileUtil, Logger logger) {
        this.logger = logger;
        this.storage = storage;
        this.fileUtil = fileUtil;
        logger.d("created");
    }

    public Observable<List<Deck>> load() {
        logger.d("loadSet decks");
        return Observable.just(storage.load());
    }

    @Override
    public Observable<List<MTGCard>> loadDeck(Deck deck) {
        logger.d("loadSet " + deck.toString());
        return Observable.just(storage.loadDeck(deck));
    }

    @Override
    public Observable<List<Deck>> addDeck(String name) {
        logger.d("create deck with name: " + name);
        return Observable.just(storage.addDeck(name));
    }

    @Override
    public Observable<List<Deck>> deleteDeck(Deck deck) {
        logger.d("delete " + deck.toString());
        return Observable.just(storage.deleteDeck(deck));
    }

    @Override
    public Observable<List<MTGCard>> editDeck(Deck deck, String name) {
        logger.d("edit " + deck.toString() + " with name: " + name);
        return Observable.just(storage.editDeck(deck, name));
    }

    @Override
    public Observable<List<MTGCard>> addCard(Deck deck, MTGCard card, int quantity) {
        logger.d("add " + quantity + " " + card.toString() + " to deck: " + deck);
        return Observable.just(storage.addCard(deck, card, quantity));
    }

    @Override
    public Observable<List<MTGCard>> addCard(String name, MTGCard card, int quantity) {
        logger.d("add " + quantity + " " + card.toString() + " to new deck with name: " + name);
        return Observable.just(storage.addCard(name, card, quantity));
    }

    @Override
    public Observable<List<MTGCard>> removeCard(Deck deck, MTGCard card) {
        logger.d("remove " + card.toString() + " from deck: " + deck);
        return Observable.just(storage.removeCard(deck, card));
    }

    @Override
    public Observable<List<MTGCard>> removeAllCard(Deck deck, MTGCard card) {
        logger.d("remove all " + card.toString() + " from deck: " + deck);
        return Observable.just(storage.removeAllCard(deck, card));
    }

    @Override
    public Observable<List<Deck>> importDeck(Uri uri) {
        logger.d("import " + uri.toString());
        try {
            return Observable.just(storage.importDeck(uri));
        } catch (Throwable throwable) {
            return Observable.error(throwable);
        }
    }

    @Override
    public Observable<Boolean> exportDeck(Deck deck, List<MTGCard> cards) {
        return Observable.just(fileUtil.downloadDeckToSdCard(deck, cards));
    }

    @Override
    public Observable<List<MTGCard>> moveCardToSideboard(Deck deck, MTGCard card, int quantity) {
        logger.d("move " + card.toString() + " to sideboard deck: " + deck);
        return Observable.just(storage.moveCardToSideboard(deck, card, quantity));
    }

    @Override
    public Observable<List<MTGCard>> moveCardFromSideboard(Deck deck, MTGCard card, int quantity) {
        logger.d("move " + card.toString() + " from sideboard deck: " + deck);
        return Observable.just(storage.moveCardFromSideboard(deck, card, quantity));
    }

}
