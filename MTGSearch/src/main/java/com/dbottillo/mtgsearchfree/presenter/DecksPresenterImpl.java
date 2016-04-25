package com.dbottillo.mtgsearchfree.presenter;

import android.net.Uri;

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
    RxWrapper<List<Deck>> deckWrapper;

    @Inject
    RxWrapper<List<MTGCard>> cardWrapper;

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
        deckWrapper.run(interactor.load(), deckObserver);
    }

    @Override
    public void loadDeck(Deck deck) {
        LOG.d("loadSet " + deck);
        cardWrapper.run(interactor.loadDeck(deck), cardsObserver);
    }

    @Override
    public void addDeck(String name) {
        LOG.d("add " + name);
        deckWrapper.run(interactor.addDeck(name), deckObserver);
    }

    @Override
    public void deleteDeck(Deck deck) {
        LOG.d("delete " + deck);
        deckWrapper.run(interactor.deleteDeck(deck), deckObserver);
    }

    @Override
    public void editDeck(Deck deck, String name) {
        LOG.d("edit " + deck + " with " + name);
        cardWrapper.run(interactor.editDeck(deck, name), cardsObserver);

    }

    @Override
    public void addCardToDeck(String name, MTGCard card, int quantity) {
        LOG.d();
        cardWrapper.run(interactor.addCard(name, card, quantity), cardsObserver);
    }

    @Override
    public void addCardToDeck(Deck deck, MTGCard card, int quantity) {
        LOG.d();
        cardWrapper.run(interactor.addCard(deck, card, quantity), cardsObserver);
    }

    @Override
    public void removeCardFromDeck(Deck deck, MTGCard card) {
        LOG.d();
        cardWrapper.run(interactor.removeCard(deck, card), cardsObserver);
    }

    @Override
    public void removeAllCardFromDeck(Deck deck, MTGCard card) {
        LOG.d();
        cardWrapper.run(interactor.removeAllCard(deck, card), cardsObserver);
    }

    @Override
    public void importDeck(Uri uri) {
        LOG.d("import "+uri.toString());
        deckWrapper.run(interactor.importDeck(uri), deckObserver);
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