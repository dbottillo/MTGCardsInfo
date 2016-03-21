package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.helper.LOG;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
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
        this.interactor = interactor;
    }

    public void getLuckyCards(int howMany) {
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

                cardsView.luckyCardsLoaded(mtgCards);
            }
        });

    }

    public void removeFromFavourite(MTGCard card) {
        Observable<int[]> obs = interactor.removeFromFavourite(card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<int[]>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(int[] ints) {
                CardsMemoryStorage.favourites = ints;
                cardsView.favIdLoaded(ints);
            }
        });
    }

    public void saveAsFavourite(MTGCard card) {
        Observable<int[]> obs = interactor.saveAsFavourite(card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<int[]>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(int[] ints) {
                CardsMemoryStorage.favourites = ints;
                cardsView.favIdLoaded(ints);
            }
        });
    }


    public void loadCards(final MTGSet set) {
        LOG.e("load cards for set: "+set.getName());
        CardsBucket currentBucket = CardsMemoryStorage.bucket;
        if (currentBucket != null && currentBucket.isValid(set.getName())) {
            LOG.e("cards in memory, just returned");
            cardsView.cardLoaded(currentBucket);
            return;
        }
        LOG.e("loading cards from database");
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
                CardsMemoryStorage.bucket = new CardsBucket(set, cards);
                cardsView.cardLoaded(CardsMemoryStorage.bucket);
            }
        });
    }

    public void init(CardsView view) {
        cardsView = view;
    }

    public void loadIdFavourites() {
        int[] currentFav = CardsMemoryStorage.favourites;
        if (currentFav != null) {
            cardsView.favIdLoaded(currentFav);
            return;
        }
        Observable<int[]> obs = interactor.loadIdFav()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe(new Subscriber<int[]>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(int[] ints) {
                CardsMemoryStorage.favourites = ints;
                cardsView.favIdLoaded(ints);
            }
        });
    }

    public void detachView() {
        subscription.unsubscribe();
    }

}