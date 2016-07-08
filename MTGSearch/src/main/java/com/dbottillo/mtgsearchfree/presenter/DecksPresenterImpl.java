package com.dbottillo.mtgsearchfree.presenter;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.mapper.DeckMapper;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.DecksView;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Func1;

public class DecksPresenterImpl implements DecksPresenter {

    DecksInteractor interactor;
    DecksView decksView;
    RxWrapper<List<Deck>> deckWrapper;
    RxDoubleWrapper<List<MTGCard>, DeckBucket> cardWrapper;

    DeckMapper deckMapper;

    @Inject
    public DecksPresenterImpl(DecksInteractor interactor, DeckMapper deckMapper,
                              RxWrapper<List<Deck>> deckWrapper,
                              RxDoubleWrapper<List<MTGCard>, DeckBucket> cardWrapper) {
        LOG.d("created");
        this.interactor = interactor;
        this.deckMapper = deckMapper;
        this.deckWrapper = deckWrapper;
        this.cardWrapper = cardWrapper;
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
        cardWrapper.runAndMap(interactor.loadDeck(deck), mapper, cardsObserver);
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
        cardWrapper.runAndMap(interactor.editDeck(deck, name), mapper, cardsObserver);
    }

    @Override
    public void addCardToDeck(String name, MTGCard card, int quantity) {
        LOG.d();
        cardWrapper.runAndMap(interactor.addCard(name, card, quantity), mapper, cardsObserver);
    }

    @Override
    public void addCardToDeck(Deck deck, MTGCard card, int quantity) {
        LOG.d();
        cardWrapper.runAndMap(interactor.addCard(deck, card, quantity), mapper, cardsObserver);
    }

    @Override
    public void removeCardFromDeck(Deck deck, MTGCard card) {
        LOG.d();
        cardWrapper.runAndMap(interactor.removeCard(deck, card), mapper, cardsObserver);
    }

    @Override
    public void removeAllCardFromDeck(Deck deck, MTGCard card) {
        LOG.d();
        cardWrapper.runAndMap(interactor.removeAllCard(deck, card), mapper, cardsObserver);
    }

    @Override
    public void importDeck(Uri uri) {
        LOG.d("import " + uri.toString());
        deckWrapper.run(interactor.importDeck(uri), deckObserver);
    }

    private Func1<List<MTGCard>, DeckBucket> mapper = new Func1<List<MTGCard>, DeckBucket>() {
        @Override
        public DeckBucket call(List<MTGCard> mtgCards) {
            return deckMapper.map(mtgCards);
        }
    };

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

    RxWrapper.RxWrapperListener<DeckBucket> cardsObserver = new RxWrapper.RxWrapperListener<DeckBucket>() {
        @Override
        public void onNext(DeckBucket bucket) {
            LOG.d();
            decksView.deckLoaded(bucket);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onCompleted() {

        }
    };

}