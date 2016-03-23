package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
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
        this.interactor = interactor;
    }

    public void init(DecksView view) {
        decksView = view;
    }

    public void loadDecks() {
        Observable<ArrayList<Deck>> obs = interactor.load()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(deckObserver);

    }

    @Override
    public void loadDeck(Deck deck) {
        Observable<ArrayList<MTGCard>> obs = interactor.loadDeck(deck)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void addDeck(String name) {
        Observable<ArrayList<Deck>> obs = interactor.addDeck(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(deckObserver);
    }

    @Override
    public void deleteDeck(Deck deck) {
        Observable<ArrayList<Deck>> obs = interactor.deleteDeck(deck)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(deckObserver);
    }

    @Override
    public void editDeck(Deck deck, String name) {
        Observable<ArrayList<MTGCard>> obs = interactor.editDeck(deck, name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void addCardToDeck(String name, MTGCard card, int quantity) {
        Observable<ArrayList<MTGCard>> obs = interactor.addCard(name, card, quantity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void addCardToDeck(Deck deck, MTGCard card, int quantity) {
        Observable<ArrayList<MTGCard>> obs = interactor.addCard(deck, card, quantity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void removeCardFromDeck(Deck deck, MTGCard card) {
        Observable<ArrayList<MTGCard>> obs = interactor.removeCard(deck, card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(cardsObserver);
    }

    @Override
    public void removeAllCardFromDeck(Deck deck, MTGCard card) {
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
            decksView.deckLoaded(cards);
        }
    };

}