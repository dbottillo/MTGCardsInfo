package com.dbottillo.mtgsearchfree.presenter;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.exceptions.MTGException;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.mapper.DeckMapper;
import com.dbottillo.mtgsearchfree.model.CardsCollection;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.DecksView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Function;

public class DecksPresenterImpl implements DecksPresenter {

    private final DecksInteractor interactor;
    private DecksView decksView;
    private final Runner<Boolean> exportWrapper;
    private final Runner<List<Deck>> deckWrapper;
    private final RunnerAndMap<CardsCollection, DeckBucket> cardWrapper;
    private final DeckMapper deckMapper;
    private final Logger logger;

    @Inject
    public DecksPresenterImpl(DecksInteractor interactor, DeckMapper deckMapper,
                              RunnerFactory runnerFactory, Logger logger) {
        this.logger = logger;
        this.interactor = interactor;
        this.deckMapper = deckMapper;
        this.exportWrapper = runnerFactory.simple();
        this.deckWrapper = runnerFactory.simple();
        this.cardWrapper = runnerFactory.withMap();
        logger.d("created");
    }

    public void init(DecksView view) {
        logger.d();
        decksView = view;
    }

    public void loadDecks() {
        logger.d();
        deckWrapper.run(interactor.load(), deckObserver);
    }

    @Override
    public void loadDeck(Deck deck) {
        logger.d("loadSet " + deck);
        cardWrapper.runAndMap(interactor.loadDeck(deck), mapper, cardsObserver);
    }

    @Override
    public void addDeck(String name) {
        logger.d("add " + name);
        deckWrapper.run(interactor.addDeck(name), deckObserver);
    }

    @Override
    public void deleteDeck(Deck deck) {
        logger.d("delete " + deck);
        deckWrapper.run(interactor.deleteDeck(deck), deckObserver);
    }

    @Override
    public void editDeck(Deck deck, String name) {
        logger.d("edit " + deck + " with " + name);
        cardWrapper.runAndMap(interactor.editDeck(deck, name), mapper, cardsObserver);
    }

    @Override
    public void addCardToDeck(String name, MTGCard card, int quantity) {
        logger.d();
        cardWrapper.runAndMap(interactor.addCard(name, card, quantity), mapper, cardsObserver);
    }

    @Override
    public void addCardToDeck(Deck deck, MTGCard card, int quantity) {
        logger.d();
        cardWrapper.runAndMap(interactor.addCard(deck, card, quantity), mapper, cardsObserver);
    }

    @Override
    public void removeCardFromDeck(Deck deck, MTGCard card) {
        logger.d();
        cardWrapper.runAndMap(interactor.removeCard(deck, card), mapper, cardsObserver);
    }

    @Override
    public void removeAllCardFromDeck(Deck deck, MTGCard card) {
        logger.d();
        cardWrapper.runAndMap(interactor.removeAllCard(deck, card), mapper, cardsObserver);
    }

    @Override
    public void moveCardFromSideBoard(Deck deck, MTGCard card, int quantity) {
        logger.d();
        cardWrapper.runAndMap(interactor.moveCardFromSideboard(deck, card, quantity), mapper, cardsObserver);
    }

    @Override
    public void moveCardToSideBoard(Deck deck, MTGCard card, int quantity) {
        logger.d();
        cardWrapper.runAndMap(interactor.moveCardToSideboard(deck, card, quantity), mapper, cardsObserver);
    }

    @Override
    public void importDeck(Uri uri) {
        logger.d("import " + uri.toString());
        deckWrapper.run(interactor.importDeck(uri), deckObserver);
    }

    @Override
    public void exportDeck(Deck deck, CardsCollection cards) {
        exportWrapper.run(interactor.exportDeck(deck, cards), new Runner.RxWrapperListener<Boolean>() {
            @Override
            public void onNext(Boolean data) {
                decksView.deckExported(data);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    private Function<CardsCollection, DeckBucket> mapper = new Function<CardsCollection, DeckBucket>() {
        @Override
        public DeckBucket apply(CardsCollection mtgCards) throws Exception {
            return deckMapper.map(mtgCards);
        }
    };

    private Runner.RxWrapperListener<List<Deck>> deckObserver = new Runner.RxWrapperListener<List<Deck>>() {
        @Override
        public void onNext(List<Deck> decks) {
            logger.d();
            decksView.decksLoaded(decks);
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof MTGException) {
                MTGException exception = (MTGException) e;
                decksView.showError(exception);
            } else {
                decksView.showError(e.getLocalizedMessage());
            }
        }

        @Override
        public void onCompleted() {

        }
    };

    private Runner.RxWrapperListener<DeckBucket> cardsObserver = new Runner.RxWrapperListener<DeckBucket>() {
        @Override
        public void onNext(DeckBucket bucket) {
            logger.d();
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