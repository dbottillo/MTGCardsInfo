package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.CardsView;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;

public class CardsPresenterImpl implements CardsPresenter {

    CardsInteractor interactor;

    CardsView cardsView;
    Subscription subscription = null;

    @Inject
    RxWrapper<List<MTGCard>> cardsWrapper;

    @Inject
    RxWrapper<int[]> favWrapper;

    public CardsPresenterImpl(CardsInteractor interactor) {
        LOG.d("created");
        MTGApp.graph.inject(this);
        this.interactor = interactor;
    }

    public void getLuckyCards(int howMany) {
        LOG.d("get lucky cards " + howMany);
        cardsWrapper.run(interactor.getLuckyCards(howMany), new RxWrapper.RxWrapperListener<List<MTGCard>>() {
            @Override
            public void onNext(List<MTGCard> mtgCards) {
                LOG.d();
                cardsView.cardLoaded(new CardsBucket("lucky", mtgCards));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void loadFavourites() {
        LOG.d();
        CardsBucket currentBucket = CardsMemoryStorage.bucket;
        if (currentBucket != null && currentBucket.isValid("fav")) {
            LOG.d("current bucket is valid, will return");
            cardsView.cardLoaded(currentBucket);
            return;
        }
        cardsWrapper.run(interactor.getFavourites(), new RxWrapper.RxWrapperListener<List<MTGCard>>() {
            @Override
            public void onNext(List<MTGCard> mtgCards) {
                LOG.d();
                CardsMemoryStorage.bucket = new CardsBucket("fav", mtgCards);
                cardsView.cardLoaded(CardsMemoryStorage.bucket);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void loadDeck(final Deck deck) {
        LOG.d("loadSet " + deck);
        cardsWrapper.run(interactor.loadDeck(deck), new RxWrapper.RxWrapperListener<List<MTGCard>>() {
            @Override
            public void onNext(List<MTGCard> mtgCards) {
                LOG.d();
                CardsMemoryStorage.bucket = new CardsBucket(deck.getName(), mtgCards);
                cardsView.cardLoaded(CardsMemoryStorage.bucket);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void doSearch(final SearchParams searchParams) {
        LOG.d("do search " + searchParams);
        cardsWrapper.run(interactor.doSearch(searchParams), new RxWrapper.RxWrapperListener<List<MTGCard>>() {
            @Override
            public void onNext(List<MTGCard> mtgCards) {
                LOG.d();
                CardsMemoryStorage.bucket = new CardsBucket(searchParams.toString(), mtgCards);
                cardsView.cardLoaded(CardsMemoryStorage.bucket);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void removeFromFavourite(MTGCard card, boolean reload) {
        LOG.d("remove " + card + " from fav");
        if (reload) {
            subscription = favWrapper.run(interactor.removeFromFavourite(card), idFavSubscriber);
        } else {
            subscription = favWrapper.run(interactor.removeFromFavourite(card), null);
        }
        if (CardsMemoryStorage.bucket.getKey().equals("fav")) {
            invalidateBucket();
        }
    }

    public void saveAsFavourite(MTGCard card, boolean reload) {
        LOG.d("save " + card + " as fav");
        if (reload) {
            subscription = favWrapper.run(interactor.saveAsFavourite(card), idFavSubscriber);
        } else {
            subscription = favWrapper.run(interactor.saveAsFavourite(card), null);
        }
        if (CardsMemoryStorage.bucket.getKey().equals("fav")) {
            invalidateBucket();
        }
    }

    private void invalidateBucket() {
        CardsMemoryStorage.bucket = null;
    }

    public void loadCards(final MTGSet set) {
        LOG.d("loadSet cards of " + set);
        CardsBucket currentBucket = CardsMemoryStorage.bucket;
        if (currentBucket != null && currentBucket.isValid(set.getName())) {
            LOG.d("current bucket is valid, will return");
            cardsView.cardLoaded(currentBucket);
            return;
        }
        LOG.d("obs created");
        cardsWrapper.run(interactor.loadSet(set), new RxWrapper.RxWrapperListener<List<MTGCard>>() {
            @Override
            public void onNext(List<MTGCard> mtgCards) {
                LOG.d();
                CardsMemoryStorage.bucket = new CardsBucket(set, mtgCards);
                cardsView.cardLoaded(CardsMemoryStorage.bucket);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void init(CardsView view) {
        LOG.d();
        cardsView = view;
    }

    public void loadIdFavourites() {
        LOG.d();
        int[] currentFav = CardsMemoryStorage.favourites;
        if (currentFav != null) {
            cardsView.favIdLoaded(currentFav);
            return;
        }
        subscription = favWrapper.run(interactor.loadIdFav(), idFavSubscriber);
    }

    public void detachView() {
        LOG.d();
        subscription.unsubscribe();
    }

    RxWrapper.RxWrapperListener<int[]> idFavSubscriber = new RxWrapper.RxWrapperListener<int[]>() {
        @Override
        public void onNext(int[] ints) {
            LOG.d();
            CardsMemoryStorage.favourites = ints;
            cardsView.favIdLoaded(ints);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onCompleted() {

        }
    };

}