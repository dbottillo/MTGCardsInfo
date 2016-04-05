package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.CardsView;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CardsPresenterImpl implements CardsPresenter {

    CardsInteractor interactor;

    CardsView cardsView;
    Subscription subscription = null;

    public CardsPresenterImpl(CardsInteractor interactor) {
        LOG.d("created");
        this.interactor = interactor;
    }

    public void getLuckyCards(int howMany) {
        LOG.d("get lucky cards " + howMany);
        Observable<ArrayList<MTGCard>> obs = interactor.getLuckyCards(howMany)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<ArrayList<MTGCard>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<MTGCard> mtgCards) {
                LOG.d();
                cardsView.cardLoaded(new CardsBucket("lucky", mtgCards));
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
        Observable<ArrayList<MTGCard>> obs = interactor.getFavourites()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<ArrayList<MTGCard>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<MTGCard> mtgCards) {
                LOG.d();
                CardsMemoryStorage.bucket = new CardsBucket("fav", mtgCards);
                cardsView.cardLoaded(CardsMemoryStorage.bucket);
            }
        });
    }

    @Override
    public void loadDeck(final Deck deck) {
        LOG.d("load " + deck);
        Observable<ArrayList<MTGCard>> obs = interactor.loadDeck(deck)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<ArrayList<MTGCard>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<MTGCard> cards) {
                LOG.d();
                CardsMemoryStorage.bucket = new CardsBucket(deck.getName(), cards);
                cardsView.cardLoaded(CardsMemoryStorage.bucket);
            }
        });
    }

    @Override
    public void doSearch(final SearchParams searchParams) {
        LOG.d("do search " + searchParams);
        Observable<ArrayList<MTGCard>> obs = interactor.doSearch(searchParams)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<ArrayList<MTGCard>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<MTGCard> cards) {
                LOG.d();
                CardsMemoryStorage.bucket = new CardsBucket(searchParams.toString(), cards);
                cardsView.cardLoaded(CardsMemoryStorage.bucket);
            }
        });
    }

    public void removeFromFavourite(MTGCard card, boolean reload) {
        LOG.d("remove " + card + " from fav");
        Observable<int[]> obs = interactor.removeFromFavourite(card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        if (reload) {
            subscription = obs.subscribe(idFavSubscriber);
        } else {
            subscription = obs.subscribe();
        }
        if (CardsMemoryStorage.bucket.getKey().equals("fav")){
            invalidateBucket();
        }
    }

    public void saveAsFavourite(MTGCard card, boolean reload) {
        LOG.d("save " + card + " as fav");
        Observable<int[]> obs = interactor.saveAsFavourite(card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        if (reload) {
            subscription = obs.subscribe(idFavSubscriber);
        } else {
            subscription = obs.subscribe();
        }
        if (CardsMemoryStorage.bucket.getKey().equals("fav")){
            invalidateBucket();
        }
    }

    private void invalidateBucket(){
        CardsMemoryStorage.bucket = null;
    }

    public void loadCards(final MTGSet set) {
        LOG.d("load cards of " + set);
        CardsBucket currentBucket = CardsMemoryStorage.bucket;
        if (currentBucket != null && currentBucket.isValid(set.getName())) {
            LOG.d("current bucket is valid, will return");
            cardsView.cardLoaded(currentBucket);
            return;
        }
        LOG.d("obs created");
        Observable<ArrayList<MTGCard>> obs = interactor.load(set)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<ArrayList<MTGCard>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<MTGCard> cards) {
                LOG.d();
                CardsMemoryStorage.bucket = new CardsBucket(set, cards);
                cardsView.cardLoaded(CardsMemoryStorage.bucket);
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
        Observable<int[]> obs = interactor.loadIdFav()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(idFavSubscriber);
    }

    public void detachView() {
        LOG.d();
        subscription.unsubscribe();
    }

    private Subscriber<int[]> idFavSubscriber = new Subscriber<int[]>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(int[] ints) {
            LOG.d();
            CardsMemoryStorage.favourites = ints;
            cardsView.favIdLoaded(ints);
        }
    };

}