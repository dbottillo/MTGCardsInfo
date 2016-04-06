package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.DecksView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DecksPresenterImpl implements DecksPresenter {

    DecksInteractor interactor;
    DecksView decksView;

    public DecksPresenterImpl(DecksInteractor interactor) {
        LOG.d("created");
        this.interactor = interactor;
    }

    public void init(DecksView view) {
        LOG.d();
        decksView = view;
    }

    public void loadDecks() {
        LOG.d();
        Observable<List<Deck>> obs = interactor.load()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(deckObserver);
    }

    @Override
    public void loadDeck(Deck deck) {
        LOG.d("loadSet " + deck);
        Observable<List<MTGCard>> obs = interactor.loadDeck(deck)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void addDeck(String name) {
        LOG.d("add " + name);
        Observable<List<Deck>> obs = interactor.addDeck(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(deckObserver);
    }

    @Override
    public void deleteDeck(Deck deck) {
        LOG.d("delete " + deck);
        Observable<List<Deck>> obs = interactor.deleteDeck(deck)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(deckObserver);
    }

    @Override
    public void editDeck(Deck deck, String name) {
        LOG.d("edit " + deck + " with " + name);
        Observable<List<MTGCard>> obs = interactor.editDeck(deck, name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void addCardToDeck(String name, MTGCard card, int quantity) {
        LOG.d();
        Observable<List<MTGCard>> obs = interactor.addCard(name, card, quantity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void addCardToDeck(Deck deck, MTGCard card, int quantity) {
        LOG.d();
        Observable<List<MTGCard>> obs = interactor.addCard(deck, card, quantity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void removeCardFromDeck(Deck deck, MTGCard card) {
        LOG.d();
        Observable<List<MTGCard>> obs = interactor.removeCard(deck, card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void removeAllCardFromDeck(Deck deck, MTGCard card) {
        LOG.d();
        Observable<List<MTGCard>> obs = interactor.removeAllCard(deck, card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    Observer<List<Deck>> deckObserver = new Observer<List<Deck>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Deck> decks) {
            LOG.d();
            decksView.decksLoaded(decks);
        }
    };

    Observer<List<MTGCard>> cardsObserver = new Observer<List<MTGCard>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<MTGCard> cards) {
            LOG.d();
            decksView.deckLoaded(cards);
        }
    };

}