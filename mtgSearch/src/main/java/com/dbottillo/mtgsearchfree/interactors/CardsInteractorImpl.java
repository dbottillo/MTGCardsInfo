package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;

import rx.Observable;

public class CardsInteractorImpl implements CardsInteractor {

    CardsStorage storage;

    public CardsInteractorImpl(CardsStorage storage) {
        this.storage = storage;
        LOG.d("created");
    }

    public Observable<ArrayList<MTGCard>> getLuckyCards(int howMany) {
        LOG.d("get lucky cards");
        return Observable.just(storage.getLuckyCards(howMany));
    }

    @Override
    public Observable<ArrayList<MTGCard>> getFavourites() {
        LOG.d("get favourites");
        return Observable.just(storage.getFavourites());
    }

    public Observable<int[]> saveAsFavourite(MTGCard card) {
        LOG.d("save as favourite");
        return Observable.just(storage.saveAsFavourite(card));
    }

    public Observable<int[]> removeFromFavourite(MTGCard card) {
        LOG.d("remove from favourite");
        return Observable.just(storage.removeFromFavourite(card));
    }

    public Observable<ArrayList<MTGCard>> load(MTGSet set) {
        LOG.d("load " + set.toString());
        return Observable.just(storage.load(set));
    }

    public Observable<int[]> loadIdFav() {
        LOG.d("load id fav");
        return Observable.just(storage.loadIdFav());
    }

    @Override
    public Observable<ArrayList<MTGCard>> loadDeck(Deck deck) {
        LOG.d("load deck " + deck.toString());
        return Observable.just(storage.loadDeck(deck));
    }

    @Override
    public Observable<ArrayList<MTGCard>> doSearch(SearchParams searchParams) {
        LOG.d("do search " + searchParams.toString());
        return Observable.just(storage.doSearch(searchParams));
    }
}

