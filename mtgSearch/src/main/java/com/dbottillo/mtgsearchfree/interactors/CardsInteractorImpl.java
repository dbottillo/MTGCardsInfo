package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.resources.MTGSet;

import java.util.ArrayList;

import rx.Observable;

public class CardsInteractorImpl implements CardsInteractor {

    CardsStorage storage;

    public CardsInteractorImpl(CardsStorage storage) {
        this.storage = storage;
    }

    public Observable<ArrayList<MTGCard>> getLuckyCards(int howMany) {
        return Observable.just(storage.getLuckyCards(howMany));
    }

    public Observable<int[]> saveAsFavourite(MTGCard card) {
        return Observable.just(storage.saveAsFavourite(card));
    }

    public Observable<int[]> removeFromFavourite(MTGCard card) {
        return Observable.just(storage.removeFromFavourite(card));
    }

    public Observable<ArrayList<MTGCard>> load(MTGSet set) {
        return Observable.just(storage.load(set));
    }

    public Observable<int[]> loadIdFav() {
        return Observable.just(storage.loadIdFav());
    }

}

