package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.DecksView;

import java.util.ArrayList;

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
        Observable<ArrayList<Deck>> obs = interactor.load()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(deckObserver);
    }

    @Override
    public void loadDeck(Deck deck) {
        LOG.d("load " + deck);
        Observable<ArrayList<MTGCard>> obs = interactor.loadDeck(deck)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void addDeck(String name) {
        LOG.d("add " + name);
        Observable<ArrayList<Deck>> obs = interactor.addDeck(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(deckObserver);
    }

    @Override
    public void deleteDeck(Deck deck) {
        LOG.d("delete " + deck);
        Observable<ArrayList<Deck>> obs = interactor.deleteDeck(deck)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(deckObserver);
    }

    @Override
    public void editDeck(Deck deck, String name) {
        LOG.d("edit " + deck + " with " + name);
        Observable<ArrayList<MTGCard>> obs = interactor.editDeck(deck, name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void addCardToDeck(String name, MTGCard card, int quantity) {
        LOG.d();
        Observable<ArrayList<MTGCard>> obs = interactor.addCard(name, card, quantity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void addCardToDeck(Deck deck, MTGCard card, int quantity) {
        LOG.d();
        Observable<ArrayList<MTGCard>> obs = interactor.addCard(deck, card, quantity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void removeCardFromDeck(Deck deck, MTGCard card) {
        LOG.d();
        Observable<ArrayList<MTGCard>> obs = interactor.removeCard(deck, card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void removeAllCardFromDeck(Deck deck, MTGCard card) {
        LOG.d();
        Observable<ArrayList<MTGCard>> obs = interactor.removeAllCard(deck, card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    Observer<ArrayList<Deck>> deckObserver = new Observer<ArrayList<Deck>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(ArrayList<Deck> decks) {

            LOG.d();
            decksView.decksLoaded(decks);
        }
    };

    Observer<ArrayList<MTGCard>> cardsObserver = new Observer<ArrayList<MTGCard>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(ArrayList<MTGCard> cards) {
            LOG.d();
            decksView.deckLoaded(cards);
        }
    };

}