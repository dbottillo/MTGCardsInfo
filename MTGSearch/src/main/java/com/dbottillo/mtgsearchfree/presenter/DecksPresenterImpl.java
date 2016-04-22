package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.DecksView;

import java.util.List;

import javax.inject.Inject;

public class DecksPresenterImpl implements DecksPresenter {

    DecksInteractor interactor;
    DecksView decksView;

    @Inject
    RxWrapper wrapper;

    public DecksPresenterImpl(DecksInteractor interactor) {
        LOG.d("created");
        MTGApp.graph.inject(this);
        this.interactor = interactor;
    }

    public void init(DecksView view) {
        LOG.d();
        decksView = view;
    }

    public void loadDecks() {
        LOG.d();
        wrapper.run(interactor.load(), deckObserver);
    }

    @Override
    public void loadDeck(Deck deck) {
        LOG.d("loadSet " + deck);
        wrapper.run(interactor.loadDeck(deck), cardsObserver);
    }

    @Override
    public void addDeck(String name) {
        LOG.d("add " + name);
        wrapper.run(interactor.addDeck(name), deckObserver);
    }

    @Override
    public void deleteDeck(Deck deck) {
        LOG.d("delete " + deck);
        wrapper.run(interactor.deleteDeck(deck), deckObserver);
    }

    @Override
    public void editDeck(Deck deck, String name) {
        LOG.d("edit " + deck + " with " + name);
        wrapper.run(interactor.editDeck(deck, name), cardsObserver);

    }

    @Override
    public void addCardToDeck(String name, MTGCard card, int quantity) {
        LOG.d();
        wrapper.run(interactor.addCard(name, card, quantity), cardsObserver);
    }

    @Override
    public void addCardToDeck(Deck deck, MTGCard card, int quantity) {
        LOG.d();
        wrapper.run(interactor.addCard(deck, card, quantity), cardsObserver);
    }

    @Override
    public void removeCardFromDeck(Deck deck, MTGCard card) {
        LOG.d();
        wrapper.run(interactor.removeCard(deck, card), cardsObserver);
    }

    @Override
    public void removeAllCardFromDeck(Deck deck, MTGCard card) {
        LOG.d();
        wrapper.run(interactor.removeAllCard(deck, card), cardsObserver);
    }


    RxWrapper.RxWrapperListener<List<Deck>> deckObserver = new RxWrapper.RxWrapperListener<List<Deck>>() {
        @Override
        public void onNext(List<Deck> decks) {
            LOG.d();
            decksView.decksLoaded(decks);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onCompleted() {

        }
    };

    RxWrapper.RxWrapperListener<List<MTGCard>> cardsObserver = new RxWrapper.RxWrapperListener<List<MTGCard>>() {
        @Override
        public void onNext(List<MTGCard> cards) {
            LOG.d();
            decksView.deckLoaded(cards);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onCompleted() {

        }
    };

}